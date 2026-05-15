# GitOps Rollout Stability Lab

This directory is for Argo CD-driven rollout experiments.

Structure:

- `apps/`
  - Argo CD `Application` manifests
- `rollout-lab/base/`
  - shared API/chat manifests
- `rollout-lab/overlays/pre-improvement/`
  - no probes, no preStop, aggressive termination
- `rollout-lab/overlays/post-improvement/`
  - readiness/liveness, safer rolling update, graceful termination

Recommended flow:

1. Commit and push this directory.
2. Install Argo CD in the cluster.
3. Apply the `Application` manifest pointing to the desired overlay.
4. Generate traffic and compare rollout behavior between overlays.
