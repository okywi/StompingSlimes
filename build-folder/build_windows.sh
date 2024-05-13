rm -rf ../../VeganLand-build/out-windows  
 
java -jar ./packr-all-4.0.0.jar --platform windows64 --jdk ./windows.zip --useZgcIfSupportedOs --executable Veganland --classpath ../desktop/build/libs/desktop-1.0.jar --mainclass de.okywi.veganland.DesktopLauncher --vmargs Xmx1G XstartOnFirstThread --resources ../assets/* --output ../../VeganLand-build/out-windows
