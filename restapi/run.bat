docker stop %2
docker run --rm -d -e PROVYSDB_URL=host.docker.internal:60002:PVYS -e PROVYSDB_USER=ealoader -e PROVYSDB_PWD=heslo -e PROVYSDB_MINPOOLSIZE=1 -e PROVYSDB_MAXPOOLSIZE=3 --publish %3:8080 --name %2 %1/%2

