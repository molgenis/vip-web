[![Build Status](https://app.travis-ci.com/molgenis/vip-web.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-web)

# vip-web

[Variant Interpretation Pipeline](https://molgenis.github.io/vip/) web interface, available
at https://vip.molgeniscloud.org.

# Developers

Repository consists of three projects:

- vip-web-backend
- vip-web-cluster
- vip-web-frontend

Application flow:

- Users create jobs using the `vip-web-frontend`
- Jobs are stored in the `vip-web-backend` database
- Compute cluster(s) can use the scripts in `vip-web-cluster` to poll `vip-web-backend` for the next available job
- The cluster processes the job using VIP
- The cluster sends back the results to VIP

The backend is not aware of the existence of any clusters, all communication is initiated by the compute cluster(s).

## Development

### Backend

#### Requirements

- Java 21

#### Running

```
java -Dspring.profiles.active=dev org.molgenis.vipweb.VipWebApplication
```

### Frontend

```
pnpm run dev
```

## Build

```
mvn clean install
```

## Deployment

### Backend & Frontend

```
VIPWEB_ADMIN_USERNAME=<username> \
VIPWEB_ADMIN_PASSWORD=<password> \
VIPWEB_DATASOURCE_URL=jdbc:h2:file:<path>/h2 \
VIPWEB_DATASOURCE_USERNAME=<username> \
VIPWEB_DATASOURCE_PASSWORD=<password> \
VIPWEB_FS_PATH=<path> \
VIPWEB_INITIALIZER_ENABLED=true \
VIPWEB_INITIALIZER_JOBS=<path_to_json> \
VIPWEB_INITIALIZER_TREES=<path_to_json> \
VIPWEB_REMEMBERME_KEY=<key> \
VIPWEB_SQL_INIT_MODE=always \
VIPWEB_VIPBOT_USERNAME=<username> \
VIPWEB_VIPBOT_PASSWORD=<password> \
java -jar -Dspring.profiles.active=production vip-web-backend-<version>.jar

```

### Cluster

Use https://github.com/molgenis/take-it-easyconfigs/tree/main/v/vip to install VIP on a cluster.

```
git clone https://github.com/molgenis/vip-web
cd vip-web/vip-web-cluster
VIPWEB_VIPBOT_USERNAME=<username> VIPWEB_VIPBOT_PASSWORD=<password> bash run.sh
```

Example cron job that checks for work every minute:

```
* * * * * VIPWEB_VIPBOT_USERNAME=<username> VIPWEB_VIPBOT_PASSWORD=<password> /usr/bin/flock -n <path>/vip-web.lockfile /bin/bash <path>/vip-web/vip-web-cluster/run.sh >> <path>/cron.log 2>&1
```