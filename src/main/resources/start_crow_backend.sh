#!/bin/bash
#############################################################
# Fichier     :  start_crow_backend.sh
# Auteur      :  ERROR23
# Email       :  error23.d@gmail.com
# OS          :  Linux
# Compilateur :  bash
# Date        :  06/07/2021
# Description :  Default crow os docker backend starter
#############################################################

LOCAL_POSTGRESQL_FOLDER=~/.crow/os
FLASHFORGE_PRINTER_IP=$1 # change me

mkdir -pv $LOCAL_POSTGRESQL_FOLDER
chmod 777 $LOCAL_POSTGRESQL_FOLDER

#######################################################################################################
docker run                                                                                            \
    -p 127.0.0.1:80:8080 -p 127.0.0.1:80:8080/udp                                                     \
    -v $LOCAL_POSTGRESQL_FOLDER:/var/lib/pgsql/                                                       \
    --env DB_USER=db_admin                                                                            \
    --env DB_PASSWORD=db_admin_changeMe                                                               \
    --env ROOT_USER_NAME=root_esp32                                                                   \
    --env ROOT_USER_LAST_NAME=root_esp32                                                              \
    --env ROOT_USER_EMAIL=root.esp@crow.com                                                           \
    --env ROOT_USER_PASSWORD=root_esp32_changeMe                                                      \
    --env FLASH_FORGE_USER_NAME="Flash Forge"                                                         \
    --env FLASH_FORGE_USER_LAST_NAME="Flash Forge"                                                    \
    --env FLASH_FORGE_USER_EMAIL=flashforge@crow.com                                                  \
    --env FLASH_FORGE_USER_PASSWORD=flash_forge_changeme                                              \
    --env PRINTER_MACHINE_IP=$FLASHFORGE_PRINTER_IP                                                   \
    --env ESP_USER_NAME=ESP32                                                                         \
    --env ESP_USER_LAST_NAME=PROD                                                                     \
    --env ESP_USER_EMAIL=esp32.prod@crow.com                                                          \
    --env ESP_USER_PASSWORD=esp_32_prod_changeMe                                                      \
    --env ESP_DEV_USER_NAME=EPS32                                                                     \
    --env ESP_DEV_USER_LAST_NAME=DEV                                                                  \
    --env ESP_DEV_USER_EMAIL=esp32.dev@crow.com                                                       \
    --env ESP_DEV_USER_PASSWORD=esp_32_dev_changeMe                                                   \
    -td error23/crow-os-backend:1.0.0                                                                     #
#######################################################################################################
