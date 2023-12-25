#!/bin/sh

ORGANIZATION_SIGNING_NAME="sample_organization"
APPLICATION_NAME="secure-markdown"
DOMAIN_FILE_NAME="domain.ext"
BACKEND_LOCATION_SSL='../../src/main/resources/'
FRONTEND_LOCATION_SSL='../../react_client/src/ssl/'

# Creating configuration files
echo "[req]
default_bits = 4096
prompt = no
default_md = sha256
distinguished_name = WUT

[WUT]
C = PL
ST = Mazovian
L = Warsaw
O = Warsaw University of Technology
OU = Electrical faculty
CN = Electrical" >> "${ORGANIZATION_SIGNING_NAME}.conf"

echo "[req]
default_bits = 4096
prompt = no
default_md = sha256
distinguished_name = Dolphin

[Dolphin]
C = PL
ST = Mazovian
L = Warsaw
O = Dolphin.sp.zoo
OU = IT
CN = dolphins" >> "${APPLICATION_NAME}.conf"

echo "authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost" >> "${DOMAIN_FILE_NAME}"

# Creating ca organization for signing the key
openssl req -x509 -newkey rsa:4096 -keyout ${ORGANIZATION_SIGNING_NAME}.key -out ${ORGANIZATION_SIGNING_NAME}.crt -config ${ORGANIZATION_SIGNING_NAME}.conf -nodes

# Creating request from the website to sign the csr
openssl req -newkey rsa:4096 -keyout "${APPLICATION_NAME}.key" -out "${APPLICATION_NAME}.csr" -config ${APPLICATION_NAME}.conf -nodes

# Creating crt signed by the sample company
openssl x509 -req -CA "${ORGANIZATION_SIGNING_NAME}".crt -CAkey ${ORGANIZATION_SIGNING_NAME}.key -in "${APPLICATION_NAME}".csr -out ${APPLICATION_NAME}.crt -days 365 -CAcreateserial -extfile "${DOMAIN_FILE_NAME}"

# Removing unnecessary files
rm *.csr *.conf *.ext

# Converting key for backend
openssl pkcs12 -inkey ${APPLICATION_NAME}.key -in ${APPLICATION_NAME}.crt -export -out ${APPLICATION_NAME}.pfx -passout pass:root
cp ${APPLICATION_NAME}.key ${APPLICATION_NAME}.pfx ${BACKEND_LOCATION_SSL}
cp ${APPLICATION_NAME}.key ${APPLICATION_NAME}.crt ${FRONTEND_LOCATION_SSL}