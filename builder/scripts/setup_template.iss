; GUIsliceBuilder Inno Setup SKELETON Script
;
; PLEASE NOTE:
;
; 1. This script is a SKELETON and is meant to be parsed by the Gradle 
;    task "innosetup" before handing it to the Inno Setup compiler (ISCC)
;
; 2. All VARIABLES with a dollar sign and curly brackets are replaced
;    by Gradle, e.g. "applicationVersion" below
;
; 3. The script is COPIED to build/innosetup before its run,
;    so all relative paths refer to this path!
;
; 4. All BACKSLASHES must be escaped 
;

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
; Right now we are not setting 64 bit mode 
;ArchitecturesInstallIn64BitMode=x64 ia64
AppId={{D2C2D2DF-35CC-4921-9F51-2F31B8C0E3C5}
AppName=GUIsliceBuilder
AppVersion=${applicationVersion}
AppVerName=GUIsliceBuilder ${applicationVersion}
AppPublisher=impulseadventure.com
AppPublisherURL=https://www.impulseadventure.com
AppSupportURL=https://github.com/ImpulseAdventure/GUIslice/issues
AppUpdatesURL=https://github.com/ImpulseAdventure/GUIslice/releases
DefaultGroupName=GUIslice
DefaultDirName={userdocs}\\GUIsliceBuilder
DisableDirPage=no
DisableWelcomePage=no
DisableProgramGroupPage=yes
LicenseFile=..\\..\\docs\\LICENSE.txt
OutputDir=.
OutputBaseFilename=builder-win-${applicationVersion}
SetupIconFile=..\\tmp\\windows\\GUIsliceBuilder\\guislicebuilder.ico
Compression=lzma
SolidCompression=yes
PrivilegesRequired=none

[Setup]
; Tell Windows Explorer to reload the environment
ChangesEnvironment=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Dirs]
Name: "{app}"; 
Name: "{app}\\logs"; Permissions: everyone-full

[Files]
Source: "..\\tmp\\windows\\GUIsliceBuilder\\GUIslice.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\\tmp\\windows\\GUIsliceBuilder\\guislicebuilder.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\\tmp\\windows\\GUIsliceBuilder\\release"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\\tmp\\windows\\GUIsliceBuilder\\bin\\*"; DestDir: "{app}\\bin"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\tmp\\windows\\GUIsliceBuilder\\conf\\*"; DestDir: "{app}\\conf"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\tmp\\windows\\GUIsliceBuilder\\legal\\*"; DestDir: "{app}\\legal"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\tmp\\windows\\GUIsliceBuilder\\lib\\*"; DestDir: "{app}\\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\tmp\\windows\\GUIsliceBuilder\\fonts\\*"; DestDir: "{app}\\fonts"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\..\\package\\templates\\*"; DestDir: "{app}\\templates"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\..\\package\\arduino_res\\*"; DestDir: "{app}\\arduino_res"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\\..\\package\\linux_res\\*"; DestDir: "{app}\\linux_res"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\\GUIsliceBuilder"; Filename: "{app}\\GUIslice.bat"
Name: "{commondesktop}\\GUIslice Builder"; Filename: "{app}\\GUIslice.bat"; IconFilename: "{app}\\guislicebuilder.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\\GUIslice.bat"; Description: "{cm:LaunchProgram,GUIsliceBuilder}"; Flags: shellexec postinstall skipifsilent

[Registry]
; set PROJECT DIRECTORY
Root: HKCU; Subkey: "Software\\JavaSoft\\Prefs\\com\\impulseadventure\\builder\\general-16"; ValueType:string; ValueName:"/Project /Directory"; ValueData:"{code:GetProjectDir}"

[Code]
var
  ProjectDirPage: TInputDirWizardPage;
  S: String;

function GetProjectDir(Value: string): string;
begin
  S := ProjectDirPage.Values[0];
  S := AnsiLowercase(S);
  StringChangeEx(S, '\\', '//', True);
  Result := S;
end;

procedure InitializeWizard;
begin
  ProjectDirPage := CreateInputDirPage(wpLicense, 'Set your Project Folder to where all projects will be kept.', 
    'Project Folder should be your Arduino Sketchbook Folder or PlatformIO Projects Folder',
    'Note that this folder can be changed later in the Builder Preferences.'#13#10 +
    'If the default is acceptable, then click Next.', False, '');
  ProjectDirPage.Add('Project Folder:');
  ProjectDirPage.Values[0] := ExpandConstant('{userdocs}\\Arduino');
end;

function IsRegularUser(): Boolean;
begin
Result := not (IsAdminLoggedOn or IsPowerUserLoggedOn);
end;

