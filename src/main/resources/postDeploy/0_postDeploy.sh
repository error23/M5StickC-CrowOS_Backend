#!/bin/bash
#############################################################
# Fichier     :  0_postDeploy.sh
# Auteur      :  ERROR23
# Email       :  error23.d@gmail.com
# OS          :  Linux
# Compilateur :  bash
# Date        :  06/07/2021
# Description :  cleans up after preDeploy script
#############################################################

# Source usefull commands
source /root/bin/init.conf

# Print welcome message
echo $blueForeGround
figlet -c CROW PASSWORD CRYPTER
echo $cyanForeGround

find -name "*.sh.export" -delete
