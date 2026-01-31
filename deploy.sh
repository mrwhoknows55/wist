#!/bin/bash
set -e

echo "Starting deployment..."

# Load environment variables if .env exists
if [ -f .env ]; then
  set -a
  source .env
  set +a
fi

# Docker image from GitHub Actions
IMAGE_REPO="ghcr.io/mrwhoknows55/wist"
IMAGE_TAG="${IMAGE_TAG:-latest}"
IMAGE="${IMAGE_REPO}:${IMAGE_TAG}"

echo "Pulling image: $IMAGE"

# Login to GHCR
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

# Pull latest image
docker pull "$IMAGE"

# Update docker-compose.yml to use the new image tag (if needed)
# This assumes your docker-compose.yml uses IMAGE_TAG env var
export IMAGE_TAG="$IMAGE_TAG"

# Restart services
echo "Restarting services..."
docker compose down
docker compose up -d

echo "Deployment complete!"

# Cleanup
docker logout ghcr.io
