param(
    [string]$BaseUrl = "http://3.39.31.114:8080",
    [string]$InstanceId = "i-08968f306a871d2a4",
    [string]$Region = "ap-northeast-2",
    [string]$RedisContainer = "integrated-login-redis",
    [string]$K6Script = "loadtest/map-main-rps10-3m.js",
    [int]$TriggerAfterSeconds = 30,
    [int]$PollIntervalSeconds = 5
)

$ErrorActionPreference = "Stop"

function Assert-CommandExists {
    param([string]$CommandName)

    if (-not (Get-Command $CommandName -ErrorAction SilentlyContinue)) {
        throw "Required command not found: $CommandName"
    }
}

function Invoke-SsmCommands {
    param([string[]]$Commands)

    $tempFile = Join-Path $env:TEMP ("ssm-command-" + [guid]::NewGuid() + ".json")
    @{ commands = $Commands } | ConvertTo-Json -Depth 3 | Set-Content -LiteralPath $tempFile

    try {
        $sendResult = & aws ssm send-command `
            --instance-ids $InstanceId `
            --document-name AWS-RunShellScript `
            --parameters ("file://" + $tempFile) `
            --region $Region | ConvertFrom-Json

        $commandId = $sendResult.Command.CommandId
        if (-not $commandId) {
            throw "Failed to get SSM command id."
        }

        while ($true) {
            Start-Sleep -Seconds 1

            $invocation = & aws ssm get-command-invocation `
                --command-id $commandId `
                --instance-id $InstanceId `
                --region $Region | ConvertFrom-Json

            switch ($invocation.Status) {
                "Pending" { continue }
                "InProgress" { continue }
                "Delayed" { continue }
                "Success" { return $invocation }
                default {
                    $errorMessage = @(
                        "SSM command failed with status $($invocation.Status).",
                        $invocation.StandardErrorContent,
                        $invocation.StandardOutputContent
                    ) -join [Environment]::NewLine
                    throw $errorMessage.Trim()
                }
            }
        }
    } finally {
        Remove-Item -LiteralPath $tempFile -ErrorAction SilentlyContinue
    }
}

function Get-ActiveVersion {
    $result = Invoke-SsmCommands -Commands @(
        "docker exec $RedisContainer redis-cli GET pointsMain:latest_version"
    )

    return $result.StandardOutputContent.Trim()
}

function Get-PointsMainKeys {
    $result = Invoke-SsmCommands -Commands @(
        "docker exec $RedisContainer redis-cli KEYS `"pointsMain*`""
    )

    return ($result.StandardOutputContent -split "`r?`n" | Where-Object { $_.Trim() })
}

function Invoke-BatchRefresh {
    return Invoke-WebRequest -Uri "$BaseUrl/api/map/test-import" -TimeoutSec 120
}

function Format-Value {
    param([string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return "<null>"
    }

    return $Value
}

Assert-CommandExists -CommandName "aws"
Assert-CommandExists -CommandName "k6"

$resolvedK6Script = Resolve-Path $K6Script
$startedAt = Get-Date
$initialVersion = Get-ActiveVersion
$initialKeys = Get-PointsMainKeys

Write-Host "=== Version Switch Load Test ==="
Write-Host "Base URL            : $BaseUrl"
Write-Host "K6 script           : $resolvedK6Script"
Write-Host "Initial version     : $(Format-Value $initialVersion)"
Write-Host "Initial keys        : $($initialKeys -join ', ')"
Write-Host "Batch trigger after : ${TriggerAfterSeconds}s"
Write-Host ""

$summaryPath = Join-Path $PWD "loadtest\k6-version-switch-summary.json"
$stdoutPath = Join-Path $PWD "loadtest\k6-version-switch.out.log"
$stderrPath = Join-Path $PWD "loadtest\k6-version-switch.err.log"

Remove-Item $summaryPath, $stdoutPath, $stderrPath -ErrorAction SilentlyContinue

$previousBaseUrl = $env:BASE_URL
$env:BASE_URL = $BaseUrl

try {
    $k6Process = Start-Process `
        -FilePath "k6" `
        -ArgumentList @("run", "--summary-export", $summaryPath, $resolvedK6Script) `
        -RedirectStandardOutput $stdoutPath `
        -RedirectStandardError $stderrPath `
        -PassThru `
        -NoNewWindow

    $triggered = $false
    $triggeredAt = $null
    $batchResponse = $null
    $versionSwitchedAt = $null
    $versionAfterSwitch = $initialVersion

    while (-not $k6Process.HasExited) {
        $elapsedSeconds = [int]((Get-Date) - $startedAt).TotalSeconds

        if (-not $triggered -and $elapsedSeconds -ge $TriggerAfterSeconds) {
            Write-Host ""
            Write-Host "[$((Get-Date).ToString('HH:mm:ss'))] Triggering batch refresh..."
            $batchResponse = Invoke-BatchRefresh
            $triggered = $true
            $triggeredAt = Get-Date
            Write-Host "Batch response      : HTTP $($batchResponse.StatusCode)"
        }

        $currentVersion = Get-ActiveVersion
        Write-Host "[$((Get-Date).ToString('HH:mm:ss'))] Active version: $(Format-Value $currentVersion)"

        if ($triggered -and -not $versionSwitchedAt -and $currentVersion -and $currentVersion -ne $initialVersion) {
            $versionSwitchedAt = Get-Date
            $versionAfterSwitch = $currentVersion
            Write-Host "Version switched    : $initialVersion -> $versionAfterSwitch"
        }

        Start-Sleep -Seconds $PollIntervalSeconds
        $k6Process.Refresh()
    }

    $k6Process.WaitForExit()
    $finalVersion = Get-ActiveVersion
    $finalKeys = Get-PointsMainKeys

    Write-Host ""
    Write-Host "=== Result ==="
    Write-Host "K6 exit code        : $($k6Process.ExitCode)"
    Write-Host "Final version       : $(Format-Value $finalVersion)"
    Write-Host "Final keys          : $($finalKeys -join ', ')"
    Write-Host "K6 summary          : $summaryPath"
    Write-Host "K6 stdout log       : $stdoutPath"
    Write-Host "K6 stderr log       : $stderrPath"

    if ($versionSwitchedAt) {
        $switchDelay = [int]($versionSwitchedAt - $triggeredAt).TotalSeconds
        Write-Host "Switch delay        : ${switchDelay}s after batch trigger"
    } else {
        Write-Warning "Version did not change during this run."
    }

    if ($k6Process.ExitCode -ne 0) {
        throw "k6 failed. Check $stderrPath"
    }
} finally {
    $env:BASE_URL = $previousBaseUrl
}
