docker stop %2
docker run --rm -d -e PROVYSDB_URL=host.docker.internal:1521:PVYS --publish %3:8080 --name %2 %1/%2

