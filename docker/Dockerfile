FROM php:8.1-cli

COPY --from=composer:latest /usr/bin/composer /usr/local/bin/composer

RUN apt-get update \
    && apt-get install -y --no-install-recommends\
       bash \
       file \
       sshpass \
       jq \
       git \
       zip \
       unzip \
       default-mysql-client \
    && docker-php-ext-install gd mysqli opcache zip intl \
    && rm -rf /var/lib/apt/lists/*