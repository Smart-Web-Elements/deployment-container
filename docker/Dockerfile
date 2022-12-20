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
       libzip4 \
       default-mysql-client \
       libfreetype6-dev \
       libtool \
       libjpeg-dev \
       libpng-dev \
       libxml2-dev \
       libicu-dev \
       libyaml-dev \
       libzip-dev \
    && docker-php-ext-install gd mysqli opcache zip intl \
    && rm -rf /var/lib/apt/lists/*