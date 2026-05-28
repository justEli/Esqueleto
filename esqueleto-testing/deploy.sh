docker build -t esqueleto-testing-jar .
docker compose -p esqueleto-testing up -d

docker logs --follow esqueleto-application
docker compose up application --build

docker exec -it postgres psql -U postgres -d esqueleto -c "SELECT * FROM test_table;"
docker exec -it mysql mysql -u root -pF14WeaG1BLKAnvIT7 esqueleto -e "SELECT * FROM test_table;"
docker exec -it mariadb mariadb -u root -pF14WeaG1BLKAnvIT7 esqueleto -e "SELECT * FROM test_table;"
docker exec -it mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P F14WeaG1BLKAnvIT7 -No -Q "SELECT * FROM test_table"
