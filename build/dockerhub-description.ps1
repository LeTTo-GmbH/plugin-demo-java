# Lädt DOCKERHUB.md als Beschreibung (Overview) des Docker-Hub-Repositories hoch.
# Benötigt einen Docker-Hub Personal Access Token mit Scope "Read & Write" für lettohub.
#   powershell -ExecutionPolicy Bypass -File build\dockerhub-description.ps1 -User <dockerhub-user>
# Der Token wird aus $env:DOCKERHUB_TOKEN gelesen oder interaktiv abgefragt.
param(
    [Parameter(Mandatory = $true)][string]$User,
    [string]$Token = $env:DOCKERHUB_TOKEN,
    [string]$Repo = "lettohub/letto-service-plugindemo",
    [string]$Short = "LeTTo Referenz-Plugin (Java/Spring Boot) mit Demo-Plugin Uhr - Vorlage für eigene Plugins",
    [string]$File = "$PSScriptRoot\..\DOCKERHUB.md"
)
$ErrorActionPreference = "Stop"
if (-not $Token) {
    $sec = Read-Host -AsSecureString "Docker-Hub Access Token für $User"
    $Token = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))
}

function ToUtf8Json($obj) { [Text.Encoding]::UTF8.GetBytes(($obj | ConvertTo-Json -Compress)) }

$login = Invoke-RestMethod -Method Post -Uri "https://hub.docker.com/v2/auth/token" `
    -ContentType "application/json; charset=utf-8" -Body (ToUtf8Json @{ identifier = $User; secret = $Token })

$body = @{
    description      = $Short
    full_description = (Get-Content -Raw -Encoding UTF8 $File)
}
$result = Invoke-RestMethod -Method Patch -Uri "https://hub.docker.com/v2/repositories/$Repo/" `
    -Headers @{ Authorization = "Bearer $($login.access_token)" } `
    -ContentType "application/json; charset=utf-8" -Body (ToUtf8Json $body)

Write-Host "Beschreibung von $Repo aktualisiert ($($result.full_description.Length) Zeichen): https://hub.docker.com/r/$Repo"
