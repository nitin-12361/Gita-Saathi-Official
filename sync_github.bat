@echo off
echo ========================================================
echo        Gita Saathi - 1-Click GitHub Live Sync
echo ========================================================
echo.

echo [1/3] Staging all modified and new files...
git add .

echo [2/3] Committing changes...
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set commit_msg=Update Gita Saathi: %datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2% %datetime:~8,2%:%datetime:~10,2%

git commit -m "%commit_msg%"

echo.
echo [3/3] Pushing to GitHub (origin main)...
git push -u origin main

echo.
if %ERRORLEVEL% EQU 0 (
    echo ========================================================
    echo   SUCCESS: Aapka saara code GitHub par LIVE sync ho gaya!
    echo ========================================================
) else (
    echo ========================================================
    echo   NOTE: Agar push fail hua, toh GitHub login check karein.
    echo ========================================================
)

echo.
pause
