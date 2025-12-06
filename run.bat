@echo off
echo Starting Restaurant Application with Maven...

set SPRING_MAIN_BANNER_MODE=console
set SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m -Dlogging.level.org.springframework=DEBUG -Dlogging.level.com.example.restaurant=TRACE"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ====================================
    echo Error starting the application.
    echo Please check the logs above for details.
    echo ====================================
    pause
)
