rm -rf ../../VeganLand-build/out-linux
 
java -jar ./packr-all-4.0.0.jar --platform linux64 --jdk ./linux.zip --useZgcIfSupportedOs --executable Veganland --classpath ../desktop/build/libs/desktop-1.0.jar --mainclass de.okywi.veganland.DesktopLauncher --vmargs Xmx1G XstartOnFirstThread --resources ../assets/* --output ../../VeganLand-build/out-linux
