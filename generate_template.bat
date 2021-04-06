echo off
REM Create empty template plugin with Config, Plugin, Overlay classes and resources folder
REM Copyright (c) JumpIfZero 2021

set /p name=Enter plugin name?:
set /p desc=Enter plugin description?:
set year=2021

set LowerCaseMacro=for /L %%n in (1 1 2) do if %%n==2 (for %%# in (a b c d e f g h i j k l m n o p q r s t u v w x y z) do set "result=!result:%%#=%%#!") else setlocal enableDelayedExpansion ^& set result=
%LowerCaseMacro%%name%

IF EXIST %name%\NUL (
   echo Not overwriting existing plugin folder %name%
   pause
   exit
) else (  
   echo Generating template for new plugin %name%
   mkdir %result%
   cd %result%

   echo /* >%result%.gradle.kts
   echo  * Copyright ^(c^) %year% JumpIfZero ^<https://github.com/JumpIfZero^>>>%result%.gradle.kts
   echo  */>>%result%.gradle.kts

   echo version = "0.0.1">> %result%.gradle.kts
   echo.>> %result%.gradle.kts
   echo project.extra["PluginName"] = "%name%">> %result%.gradle.kts
   echo project.extra["PluginDescription"] = "%desc%">> %result%.gradle.kts
   echo project.extra["ProjectSupportUrl"] = "https://github.com/JumpIfZero">> %result%.gradle.kts
   echo.>> %result%.gradle.kts
   echo tasks {>> %result%.gradle.kts
   echo     jar {>> %result%.gradle.kts
   echo         manifest {>> %result%.gradle.kts
   echo             attributes^(mapOf^(>> %result%.gradle.kts
   echo                     "Plugin-Version" to project.version,>> %result%.gradle.kts
   echo                     "Plugin-Id" to nameToId^(project.extra["PluginName"] as String^),>> %result%.gradle.kts
   echo                     "Plugin-Provider" to project.extra["PluginProvider"],>> %result%.gradle.kts
   echo                     "Plugin-Description" to project.extra["PluginDescription"],>> %result%.gradle.kts
   echo                     "Plugin-License" to project.extra["PluginLicense"]>> %result%.gradle.kts
   echo             ^)^)>> %result%.gradle.kts
   echo         }>> %result%.gradle.kts
   echo     }>> %result%.gradle.kts
   echo }>> %result%.gradle.kts

   mkdir src
   cd /d src & mkdir main
   cd /d main & mkdir java
   cd /d java & mkdir net
   cd /d net & mkdir runelite
   cd /d runelite & mkdir client
   cd /d client & mkdir plugins
   cd /d plugins & mkdir %result%

   cd %result%

   echo /* >%name%Plugin.java
   echo  * Copyright ^(c^) %year% JumpIfZero ^<https://github.com/JumpIfZero^>>>%name%Plugin.java
   echo  */>>%name%Plugin.java
   echo package net.runelite.client.plugins.%result%;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo import com.google.inject.Provides;>>%name%Plugin.java
   echo import lombok.extern.slf4j.Slf4j;>>%name%Plugin.java
   echo import net.runelite.client.plugins.Plugin;>>%name%Plugin.java
   echo import net.runelite.client.plugins.PluginDescriptor;>>%name%Plugin.java
   echo import net.runelite.client.config.ConfigManager;>>%name%Plugin.java
   echo import net.runelite.client.ui.overlay.OverlayManager;>>%name%Plugin.java
   echo import org.pf4j.Extension;>>%name%Plugin.java
   echo import javax.inject.Inject;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo @Extension>>%name%Plugin.java
   echo @PluginDescriptor^(>>%name%Plugin.java
   echo       name = "%name%",>>%name%Plugin.java
   echo       description = "%desc%",>>%name%Plugin.java
   echo       tags = {"jz"},>>%name%Plugin.java
   echo       enabledByDefault = false>>%name%Plugin.java
   echo ^)>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo @Slf4j>>%name%Plugin.java
   echo public class %name%Plugin extends Plugin>>%name%Plugin.java
   echo {>>%name%Plugin.java
   echo    @Inject>>%name%Plugin.java
   echo    private %name%Config config;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Inject>>%name%Plugin.java
   echo    private %name%Overlay overlay;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Inject>>%name%Plugin.java
   echo    private ConfigManager configManager;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Inject>>%name%Plugin.java
   echo    private OverlayManager overlayManager;>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Provides>>%name%Plugin.java
   echo    %name%Config provideConfig^(ConfigManager configManager^)>>%name%Plugin.java
   echo    {>>%name%Plugin.java
   echo       return configManager.getConfig^(%name%Config.class^);>>%name%Plugin.java
   echo    }>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Override>>%name%Plugin.java
   echo    protected void startUp^(^)>>%name%Plugin.java
   echo    {>>%name%Plugin.java
   echo       overlayManager.add^(overlay^);>>%name%Plugin.java
   echo    }>>%name%Plugin.java
   echo.>>%name%Plugin.java
   echo    @Override>>%name%Plugin.java
   echo    protected void shutDown^(^)>>%name%Plugin.java
   echo    {>>%name%Plugin.java
   echo       overlayManager.remove^(overlay^);>>%name%Plugin.java
   echo    }>>%name%Plugin.java
   echo }>>%name%Plugin.java

   echo /* >%name%Config.java
   echo  * Copyright ^(c^) %year% JumpIfZero ^<https://github.com/JumpIfZero^>>>%name%Config.java
   echo  */>>%name%Config.java
   echo package net.runelite.client.plugins.%result%;>>%name%Config.java
   echo.>>%name%Config.java
   echo import net.runelite.client.config.Config;>>%name%Config.java
   echo import net.runelite.client.config.ConfigGroup;>>%name%Config.java
   echo import net.runelite.client.config.ConfigItem;>>%name%Config.java
   echo.>>%name%Config.java
   echo @ConfigGroup^("%result%"^)>>%name%Config.java
   echo public interface %name%Config extends Config>>%name%Config.java
   echo {>>%name%Config.java
   echo     @ConfigItem^(>>%name%Config.java
   echo 		    position = 0,>>%name%Config.java
   echo 		    keyName = "example",>>%name%Config.java
   echo 		    name = "Example Int",>>%name%Config.java
   echo 		    description = "This is example integer">>%name%Config.java
   echo     ^)>>%name%Config.java
   echo     default int example^(^)>>%name%Config.java
   echo     {>>%name%Config.java
   echo         return 0;>>%name%Config.java
   echo     }>>%name%Config.java
   echo }>>%name%Config.java

   echo /* >%name%Overlay.java
   echo  * Copyright ^(c^) %year% JumpIfZero ^<https://github.com/JumpIfZero^>>>%name%Overlay.java
   echo  */>>%name%Overlay.java
   echo package net.runelite.client.plugins.%result%;>>%name%Overlay.java
   echo.>>%name%Overlay.java
   echo import lombok.extern.slf4j.Slf4j;>>%name%Overlay.java
   echo import net.runelite.client.ui.overlay.*;>>%name%Overlay.java
   echo import javax.inject.Inject;>>%name%Overlay.java
   echo import java.awt.Dimension;>>%name%Overlay.java
   echo import java.awt.Graphics2D;>>%name%Overlay.java
   echo.>>%name%Overlay.java
   echo @Slf4j>>%name%Overlay.java
   echo public class %name%Overlay extends Overlay>>%name%Overlay.java
   echo {>>%name%Overlay.java
   echo     @Inject>>%name%Overlay.java
   echo     %name%Overlay^(^)>>%name%Overlay.java
   echo     {>>%name%Overlay.java
   echo         setPriority^(OverlayPriority.HIGHEST^);>>%name%Overlay.java
   echo         setLayer^(OverlayLayer.ABOVE_WIDGETS^);>>%name%Overlay.java
   echo         setPosition^(OverlayPosition.DYNAMIC^);>>%name%Overlay.java
   echo     }>>%name%Overlay.java
   echo.>>%name%Overlay.java
   echo     @Override>>%name%Overlay.java
   echo     public Dimension render^(Graphics2D graphics^)>>%name%Overlay.java
   echo     {>>%name%Overlay.java
   echo         return null;>>%name%Overlay.java
   echo     }>>%name%Overlay.java
   echo }>>%name%Overlay.java

   cd ../../../../../..
   mkdir resources
   cd /d resources & mkdir net
   cd /d net & mkdir runelite
   cd /d runelite & mkdir client
   cd /d client & mkdir plugins
   cd /d plugins & mkdir %result%
   
   pause
)