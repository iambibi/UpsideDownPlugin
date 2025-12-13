@echo off
setlocal enabledelayedexpansion

:: Vérification des variables d'environnement
if "%SSHPASS%"=="" (
    echo SSHPASS is not set. Use: setx SSHPASS your_password
    exit /b 1
)
if "%SERVER_ID%"=="" (
    echo SERVER_ID is not set.
    exit /b 1
)
if "%PTERO_API_KEY%"=="" (
    echo PTERO_API_KEY is not set.
    exit /b 1
)
if "%PTERO_LINK%"=="" (
    echo PTERO_LINK is not set.
    exit /b 1
)
if "%PTERO_USER%"=="" (
    echo PTERO_USER is not set.
    exit /b 1
)
if "%FILE_TO_SEND%"=="" (
    echo FILE_TO_SEND is not set.
    exit /b 1
)
if "%PTERO_PLUGINS_DIR%"=="" (
    echo PTERO_PLUGINS_DIR is not set.
    exit /b 1
)
if "%PTERO_NODE%"=="" (
    echo PTERO_NODE is not set.
    exit /b 1
)

echo Sending %FILE_TO_SEND% to Pterodactyl server...

:: Utilisation de pscp (PuTTY). Assurez-vous que pscp.exe est dans le PATH.
pscp.exe -P 2022 -pw "%SSHPASS%" "%FILE_TO_SEND%" %PTERO_USER%.%SERVER_ID%@%PTERO_NODE%:/%PTERO_PLUGINS_DIR%

if errorlevel 1 (
    echo Failed to send file.
    exit /b 1
)

echo File sent.

:: Récupération de la réponse JSON
for /f "delims=" %%i in ('powershell -Command "Invoke-RestMethod -Uri 'https://%PTERO_LINK%/api/client/servers/%SERVER_ID%/resources' -Headers @{Authorization='Bearer %PTERO_API_KEY%'} | Select-Object -ExpandProperty attributes | Select-Object -ExpandProperty current_state"') do set CURRENT_STATE=%%i

if "%CURRENT_STATE%"=="starting" (
    echo Server is starting, waiting 60 seconds...
    timeout /t 60 >nul
) else if "%CURRENT_STATE%"=="stopping" (
    echo Server is stopping, waiting 60 seconds...
    timeout /t 60 >nul
) else if "%CURRENT_STATE%"=="offline" (
    echo Server is offline, starting it...
    curl -X POST "https://%PTERO_LINK%/api/client/servers/%SERVER_ID%/power" ^
      -H "Authorization: Bearer %PTERO_API_KEY%" ^
      -H "Content-Type: application/json" ^
      -d "{\"signal\":\"start\"}"
    echo Waiting 60 seconds for the server to start...
    timeout /t 60 >nul
) else if "%CURRENT_STATE%"=="running" (
    echo Server is running, restarting it...
    curl -X POST "https://%PTERO_LINK%/api/client/servers/%SERVER_ID%/power" ^
      -H "Authorization: Bearer %PTERO_API_KEY%" ^
      -H "Content-Type: application/json" ^
      -d "{\"signal\":\"restart\"}"
) else (
    echo Unknown server state: %CURRENT_STATE%
    exit /b 1
)

exit /b 0
