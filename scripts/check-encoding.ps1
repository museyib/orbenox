param(
  [string[]]$Paths
)

$defaultPaths = @(
  "web-client/src/locales/*.json",
  "web-api/src/main/resources/i18n/messages_*.properties"
)

if (-not $Paths -or $Paths.Count -eq 0) {
  $Paths = $defaultPaths
}

$utf8 = New-Object System.Text.UTF8Encoding($false, $true)
$replacementChar = [char]0xFFFD
$suspectSequences = @(
  [char]0x00C3,
  [char]0x00C2,
  [char]0x00C4,
  [char]0x00C5,
  [char]0x00D0,
  [char]0x00D1
)
$problems = @()

$files = Get-ChildItem -Path $Paths -File -ErrorAction SilentlyContinue
foreach ($file in $files) {
  try {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    $text = $utf8.GetString($bytes)
  } catch {
    $problems += "$($file.FullName): invalid UTF-8 bytes"
    continue
  }

  if ($text.Contains($replacementChar)) {
    $problems += "$($file.FullName): contains Unicode replacement characters (likely decoding errors)"
    continue
  }

  foreach ($seq in $suspectSequences) {
    if ($text.Contains($seq)) {
      $problems += "$($file.FullName): contains possible mojibake sequence '$seq'"
      break
    }
  }
}

if ($problems.Count -gt 0) {
  Write-Error "Encoding check failed:`n- " + ($problems -join "`n- ")
  exit 1
}

Write-Host "Encoding check passed."
