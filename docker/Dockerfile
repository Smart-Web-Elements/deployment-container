FROM php:7.4-cli

ADD https://github.com/mlocati/docker-php-extension-installer/releases/latest/download/install-php-extensions /usr/local/bin/

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       bash \
       file \
       git \
       sshpass \
       wget \
       curl \
       jq \
    && apt-get clean \
    && apt-get -y autoremove \
    && rm -rf \
       /var/lib/apt/lists/* \
       /tmp/* \
       /var/tmp/* \
       /usr/local/src/*
