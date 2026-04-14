# GitHub OIDC Deploy Setup

Use GitHub Actions OIDC instead of long-lived AWS keys.

## GitHub Secret

- `AWS_GITHUB_ACTIONS_ROLE_ARN`: IAM role ARN assumed by `.github/workflows/deploy.yml`

## AWS Setup

1. Create or verify the IAM OIDC provider for `token.actions.githubusercontent.com`.
2. Create an IAM role for GitHub Actions using `github-actions-oidc-trust-policy.json`.
3. Attach a policy based on `github-actions-ssm-deploy-policy.json`.
4. Confirm `jubjub-server` is online in AWS Systems Manager.

## Deploy Flow

1. GitHub Actions assumes the IAM role through OIDC.
2. The workflow builds the Spring Boot JAR and Docker image on the GitHub runner.
3. The workflow pushes the image to Amazon ECR.
4. The workflow sends an `AWS-RunShellScript` command to `i-08968f306a871d2a4`.
5. The instance logs in to ECR, pulls the pushed image, writes the deployment files sent by the runner, and restarts the services with `docker compose`.
