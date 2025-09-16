FROM php:8.2-cli

COPY --from=composer:latest /usr/bin/composer /usr/local/bin/composer

COPY --from=1password/op:2 /usr/local/bin/op /usr/local/bin/op

COPY --from=mlocati/php-extension-installer /usr/bin/install-php-extensions /usr/local/bin/

RUN apt-get update \
    && apt-get install -y --no-install-recommends\
       bash \
       file \
       sshpass \
       openssh-client \
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
       graphicsmagick \
       ghostscript \
       npm \
    && install-php-extensions gd mysqli opcache zip intl ftp \
    && rm -rf /var/lib/apt/lists/*

# Set default PHP memory limit to 1GB
RUN echo "memory_limit=1G" > /usr/local/etc/php/conf.d/memory-limit.ini
