#!/bin/bash

export J2EE_HOME=/opt/glassfish4/glassfish
file=/opt/si2/virtualip.sh
if [ -e "$file" ]; then
    sudo /opt/si2/virtualip.sh enp1s0
else
    sudo bash virtualip.sh wlp5s0
fi
