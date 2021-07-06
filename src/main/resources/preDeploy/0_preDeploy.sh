#!/bin/bash
#############################################################
# Fichier     :  0_preDeploy.sh
# Auteur      :  ERROR23
# Email       :  error23.d@gmail.com
# OS          :  Linux
# Compilateur :  bash
# Date        :  04/07/2021
# Description :  predeploy docker script
#############################################################

# Source usefull commands
source /root/bin/init.conf

# Print welcome message
echo $blueForeGround
figlet -c CROW PASSWORD CRYPTER
echo $cyanForeGround

ROOT_USER_PASSWORD=$(bcrypt.sh "$ROOT_USER_PASSWORD" | cut -d: -f2)
FLASH_FORGE_USER_PASSWORD=$(bcrypt.sh "$FLASH_FORGE_USER_PASSWORD" | cut -d: -f2)
ESP_DEV_USER_PASSWORD=$(bcrypt.sh "$ESP_DEV_USER_PASSWORD" | cut -d: -f2)
ESP_USER_PASSWORD=$(bcrypt.sh "$ESP_USER_PASSWORD" | cut -d: -f2)

{
	echo "#!/bin/false"
	echo "export ROOT_USER_PASSWORD='$ROOT_USER_PASSWORD'"
	echo "export FLASH_FORGE_USER_PASSWORD='$FLASH_FORGE_USER_PASSWORD'"
	echo "export ESP_DEV_USER_PASSWORD='$ESP_DEV_USER_PASSWORD'"
	echo "export ESP_USER_PASSWORD='$ESP_USER_PASSWORD'"
} >> $0.export
