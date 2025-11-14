; UploadFile.au3
; Usage: UploadFile.exe "C:\full\path\to\file.ext"

Opt("WinTitleMatchMode", 2) ; partial match on window title/class
Global $filePath = ""

If $CmdLine[0] >= 1 Then
    $filePath = $CmdLine[1]
Else
    Exit 1
EndIf

; Wait for the common file dialog (#32770). Titles vary: "Open", "File Upload", etc.
Local $hWnd = WinWaitActive("[CLASS:#32770]", "", 10)
If Not $hWnd Then
    WinActivate("Open")
    $hWnd = WinWaitActive("Open", "", 5)
EndIf

If Not $hWnd Then
    WinActivate("File Upload")
    $hWnd = WinWaitActive("File Upload", "", 5)
EndIf

If Not $hWnd Then
    Exit 2 ; couldn't find dialog
EndIf

; Set the file path into the edit box and click Open
ControlFocus($hWnd, "", "Edit1")
ControlSetText($hWnd, "", "Edit1", $filePath)

; Click Open (usually Button1). On some locales, might be Button2.
If ControlClick($hWnd, "", "Button1") = 0 Then
    ControlClick($hWnd, "", "Button2")
EndIf

Exit 0
