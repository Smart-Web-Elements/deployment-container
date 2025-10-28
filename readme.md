# Deployment Container (PHP Tooling Image)

A lightweight, batteries-included Docker image for CI/CD and deployment tasks in PHP projects. It ships with PHP CLI (7.4–8.4 via build arg), Composer, 1Password CLI (`op`), common PHP extensions, MySQL/MariaDB client tools, SSH utilities, Git, `jq`, `zip`/`unzip`, GraphicsMagick, Ghostscript, and `npm` — making it a great base for build and release pipelines.

---

## Stack & Image Contents
- Base: `php:<PHP_VERSION>-cli` (supports 7.4, 8.0, 8.1, 8.2, 8.3, 8.4 via build matrix)
- Package manager(s): `apt`, `composer`, `npm`
- Tools preinstalled:
  - Composer (latest)
  - 1Password CLI (`op` v2)
  - PHP Extension Installer (`install-php-extensions`)
  - Git, OpenSSH client, `sshpass`, `jq`, `zip`, `unzip`, `file`
  - MySQL/MariaDB client (`default-mysql-client`)
  - GraphicsMagick, Ghostscript
  - `npm`
- PHP extensions:
  - `gd`, `mysqli`, `opcache`, `zip`, `intl`, `ftp`, `pdo_mysql`
- Defaults at image build time:
  - PHP `memory_limit = 1G`
  - MySQL client config disables SSL by default (see Environment Variables & Config)

There is no custom entrypoint; the container runs whatever command you pass (defaults to the base image behavior).

---

## Repository Structure
```
.
├─ docker/
│  └─ Dockerfile        # Image definition
├─ .github/
│  └─ workflows/
│     └─ docker-publish.yml  # GitHub Actions workflow to build & push images
└─ readme.md
```

---

## Requirements
- Docker 20.10+ (or a compatible container runtime)
- (Optional) Access to GitHub Container Registry (GHCR) for pushing images
- (Optional) GitHub Actions if you want CI-based builds

---

## Build Locally
- Build for a specific PHP version (default in Dockerfile is 8.4):
  ```bash
  docker build \
    --build-arg PHP_VERSION=8.4 \
    -t ghcr.io/<owner>/build:php-8.4 \
    -f ./docker/Dockerfile .
  ```

- Test the image:
  ```bash
  docker run --rm ghcr.io/<owner>/build:php-8.4 php -v
  docker run --rm ghcr.io/<owner>/build:php-8.4 composer --version
  docker run --rm ghcr.io/<owner>/build:php-8.4 op --version
  ```

- Use the image for a one-off command (example: install Composer deps):
  ```bash
  docker run --rm -v "$PWD":/app -w /app ghcr.io/<owner>/build:php-8.4 \
    bash -lc 'composer install --no-interaction --prefer-dist'
  ```

---

## Publish to GHCR (Manual)
1. Log in to GHCR:
   ```bash
   echo "$GH_PAT" | docker login ghcr.io -u <github-username> --password-stdin
   ```
2. Tag and push:
   ```bash
   IMAGE=ghcr.io/<owner>/build:php-8.4
   docker push "$IMAGE"
   ```

Replace `<owner>` with your GitHub org or username. GHCR requires the image path to be lowercase.

---

## CI/CD: GitHub Actions
This repository includes a workflow that builds and publishes images to GHCR for multiple PHP versions on pushes to `main`.

- Workflow: `.github/workflows/docker-publish.yml`
- Matrix: `['7.4', '8.0', '8.1', '8.2', '8.3', '8.4']`
- Key step (simplified):
  ```yaml
  - name: Build and push Docker image
    run: |
      IMAGE=ghcr.io/${{ github.repository_owner }}/build:php-${{ matrix.php_version }}
      IMAGE=$(echo "$IMAGE" | tr '[:upper:]' '[:lower:]')
      docker build --build-arg PHP_VERSION=${{ matrix.php_version }} -t "$IMAGE" -f ./docker/Dockerfile .
      docker push "$IMAGE"
  ```

Permissions are set to allow pushing to GHCR using `${{ secrets.GITHUB_TOKEN }}`.

---

## Environment Variables & Configuration
While building/running the image you may find these useful:

- PHP configuration
  - `memory_limit` is set to 1G via `/usr/local/etc/php/conf.d/memory-limit.ini`.
    - Override by mounting your own ini: `-v $PWD/php.ini:/usr/local/etc/php/conf.d/override.ini:ro`.

- MySQL/MariaDB client configuration
  - SSL is disabled by default via `/etc/mysql/conf.d/disable-ssl.cnf` containing:
    ```
    [client]
    skip-ssl=true

    [mysqldump]
    skip-ssl=true
    ```
  - Override by mounting your own config:
    ```bash
    docker run --rm \
      -v $PWD/my.cnf:/etc/mysql/conf.d/custom.cnf:ro \
      ghcr.io/<owner>/build:php-8.4 mysql --ssl-mode=REQUIRED -h <host>
    ```

- 1Password CLI (`op`)
  - Typical variables: `OP_SERVICE_ACCOUNT_TOKEN` for service accounts, or use interactive sign-in.
  - Example:
    ```bash
    docker run --rm -e OP_SERVICE_ACCOUNT_TOKEN \
      ghcr.io/<owner>/build:php-8.4 op item get "My Secret"
    ```

- Composer
  - Use `COMPOSER_AUTH` for private registries:
    ```bash
    docker run --rm -e COMPOSER_AUTH \
      -v "$PWD":/app -w /app \
      ghcr.io/<owner>/build:php-8.4 composer install
    ```

- SSH utilities
  - Use standard `SSH_*` envs or mount keys: `-v ~/.ssh:/root/.ssh:ro`.

---

## Common Commands (Scripts)
There are no project-local shell scripts; use Docker directly:

- Build (local):
  ```bash
  docker build --build-arg PHP_VERSION=8.4 -t ghcr.io/<owner>/build:php-8.4 -f docker/Dockerfile .
  ```
- Run PHP/Composer commands:
  ```bash
  docker run --rm ghcr.io/<owner>/build:php-8.4 php -v
  docker run --rm ghcr.io/<owner>/build:php-8.4 composer --version
  ```
- Run with workspace mounted:
  ```bash
  docker run --rm -v "$PWD":/app -w /app ghcr.io/<owner>/build:php-8.4 bash -lc 'composer install && npm --version'
  ```

---

## Entry Points
- No custom entrypoint. The image executes the command you provide. Examples:
  - `php -v`, `composer install`, `op --version`, `mysql --help`, `npm ci`.

---

## Tests
This repository contains no tests. If you adopt this image in a larger project, consider adding integration tests that build the image and validate expected tools/versions are present.

---

## License
No license file is present in this repository. If you intend the image to be open source, add a `LICENSE` file (for example, MIT). Until then, all rights are reserved by the repository owner.

---

## Support & Contributions
- Issues and PRs are welcome. Please ensure changes keep the image lean and CI-friendly.

---

## Notes
- Current local date/time when this README was generated: 2025-10-28 13:41.
