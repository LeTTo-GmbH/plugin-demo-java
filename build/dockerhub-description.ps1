# Lädt DOCKERHUB.md als Beschreibung (Overview) des Docker-Hub-Repositories hoch.
#   powershell -ExecutionPolicy Bypass -File build\dockerhub-description.ps1
# Zugangsdaten in dieser Reihenfolge:
#   1. -User und -Token (bzw. $env:DOCKERHUB_TOKEN), Token = Personal Access Token mit "Read & Write"
#   2. die Docker-Desktop-Anmeldung aus dem Credential-Store (credsStore in ~/.docker/config.json)
#   3. interaktive Abfrage
param(
    [string]$User,
    [string]$Token = $env:DOCKERHUB_TOKEN,
    [string]$Repo = "lettohub/letto-service-plugindemo",
    [string]$Short = "LeTTo Referenz-Plugin (Java/Spring Boot) mit Demo-Plugin Uhr - Vorlage für eigene Plugins",
    [string]$File = "$PSScriptRoot\..\DOCKERHUB.md"
)
$ErrorActionPreference = "Stop"

# liest Benutzer und Kennwort/Token für Docker Hub aus dem Credential-Helper von Docker
function Get-DockerCredential {
    $config = Join-Path $env:USERPROFILE ".docker\config.json"
    if (-not (Test-Path $config)) { return $null }
    $store = (Get-Content -Raw $config | ConvertFrom-Json).credsStore
    if (-not $store) { return $null }
    $psi = New-Object Diagnostics.ProcessStartInfo "docker-credential-$store", "get"
    $psi.UseShellExecute = $false
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    try { $p = [Diagnostics.Process]::Start($psi) } catch { return $null }
    # ohne Zeilenumbruch schreiben, sonst findet der Helper den Eintrag nicht
    $p.StandardInput.Write("https://index.docker.io/v1/")
    $p.StandardInput.Close()
    $out = $p.StandardOutput.ReadToEnd()
    $p.WaitForExit()
    if ($p.ExitCode -ne 0) { return $null }
    $out | ConvertFrom-Json
}

if (-not $Token) {
    $cred = Get-DockerCredential
    if ($cred -and (-not $User -or $User -eq $cred.Username)) {
        $User = $cred.Username
        $Token = $cred.Secret
        Write-Host "verwende die Docker-Desktop-Anmeldung von $User"
    }
}
if (-not $User) { $User = Read-Host "Docker-Hub-Benutzer" }
if (-not $Token) {
    $sec = Read-Host -AsSecureString "Docker-Hub Access Token für $User"
    $Token = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))
}

# Komma-Operator, damit PowerShell das byte[] nicht in ein Object[] entpackt
function ToUtf8Json($obj) { , [Text.Encoding]::UTF8.GetBytes(($obj | ConvertTo-Json -Compress)) }

$login = Invoke-RestMethod -Method Post -Uri "https://hub.docker.com/v2/auth/token" `
    -ContentType "application/json; charset=utf-8" -Body (ToUtf8Json @{ identifier = $User; secret = $Token })

$body = @{
    description      = $Short
    # nicht Get-Content: dessen String trägt PSPath & Co., die ConvertTo-Json mitserialisiert
    full_description = [IO.File]::ReadAllText((Resolve-Path $File).ProviderPath, [Text.Encoding]::UTF8)
}
Invoke-RestMethod -Method Patch -Uri "https://hub.docker.com/v2/repositories/$Repo/" `
    -Headers @{ Authorization = "Bearer $($login.access_token)" } `
    -ContentType "application/json; charset=utf-8" -Body (ToUtf8Json $body) | Out-Null

Write-Host "Beschreibung von $Repo aktualisiert: https://hub.docker.com/r/$Repo"
