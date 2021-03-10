definition(
    name: "Consistent Colors",
    namespace: "raynerprogramming",
    author: "Matt Rayner",
    description: "",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "")

preferences {
    page(name: "pageConfig")
}

def pageConfig() {
    dynamicPage(name: "", title: "", install: true, uninstall: true, refreshInterval:0) {        
    section("") {
        input "rgbDevices", "capability.colorMode", title: "RGBs", multiple: true, required: true
        input "master", "capability.colorMode", title: "Master RGB", multiple: false, required: true
        }    
    }
}

def installed() {
    log.debug "installed"
    initialize()
}

def updated() {
    log.debug "updated"
    initialize()
}

def initialize() {
    log.debug "initialize"
    subscribe(rgbDevices, "switch.on", handleDeviceEvent)
    subscribe(master, "level", handleChangeEvent)
    subscribe(master, "colorMode", handleChangeEvent)
    subscribe(master, "hue", handleChangeEvent)
    subscribe(master, "RGB", handleChangeEvent)
    subscribe(master, "colorTemperature", handleChangeEvent)
    subscribe(master, "switch", handleChangeEvent)
    unschedule()
}

def uninstalled() {
    log.debug "uninstalled"
}

def checkDevices() {
    log.debug "checkDevices"
}

def handleDeviceEvent(evt) {
    log.debug "handleDeviceEvent"
    updateDevice(evt.getDevice())
}

def handleChangeEvent(evt) {
    log.debug "handleChangeEvent"
    log.debug master
    rgbDevices.each {
        log.debug it
        if (it.currentValue("switch") == "on"){
            updateDevice(it)
        }
    }    
}

def updateDevice(current) {
    log.debug "update device " + current + " from " + master
    log.debug "new color " + master.currentColor
    log.debug "new colorTemperature " + master.currentColorTemperature
    log.debug "new level " + master.currentLevel   
    current.setLevel(master.currentLevel)     
    if(master.currentColorMode == 'CT'){
        log.debug "new color mode CT"        
        current.setColorTemperature(master.currentColorTemperature)
    }else{
        log.debug "new color mode RGB"
        current.setColor([hue:master.currentHue,saturation:master.currentSaturation,level:master.currentLevel]); 
    }
    // log.debug "Consistent Colors: setting ${current.displayName} level to ${master.currentLevel}"
    // log.debug "Consistent Colors: setting ${current.displayName} colorMode to ${master.currentColorMode}"
    // log.debug "Consistent Colors: setting ${current.displayName} colorTemperature to ${master.currentColorTemperature}"
    // log.debug "Consistent Colors: setting ${current.displayName} color to ${master.currentColor}"    
}