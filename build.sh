

echo "Сборка проекта..."


echo "Очистка проекта..."
mvn clean


echo "Сборка JAR файла..."
mvn package -DskipTests

echo "Сборка завершена. JAR файл находится в target/"