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
    atomicState.instanceCount = 0
    log.debug "initialized state to: " +atomicState.instanceCount
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
    atomicState.instanceCount = atomicState.instanceCount + 1
    thisinstance = atomicState.instanceCount 
    log.debug "Consistent Colors handleChangeEvent: " + thisinstance
    log.debug "handleChangeEvent"
    log.debug master
    tempDevices = rgbDevices.findAll{it.currentValue("switch") == "on"}
    tempMaster = master
    while(tempDevices){
        if(thisinstance != atomicState.instanceCount){
                log.debug "new instance detected, breaking instance: " + thisinstance
                break;
            }
        log.debug "====================================================="
        log.debug "Consistent Colors handleChangeEvent: " + thisinstance
        log.debug "====================================================="
        log.debug "temp devices " + tempDevices.size() + " " + tempDevices  
        tempDevices.each {
            log.debug "----------------------------------------------------------"
            log.debug it            
            if (it.currentValue("switch") == "on"){
                if(deviceEqualMaster(it)){
                    log.debug "removing device " + it
                    tempDevices -= it
                }else{
                    updateDevice(it)
                }            
            }else{
                log.debug "off"
            }
        }
        pause(15000)
        if(!deviceEqualMaster(tempMaster)){
            log.debug "detected change to master, breaking current loop"
            break
        }
    }
}
def deviceEqualMaster(device){    
    currentLevel = device.currentValue("level", true)
    currentColorMode = device.currentValue("colorMode", true)
    currentColorTemperature = device.currentValue("colorTemperature", true)
    currentHue = device.currentValue("hue", true)
    currentSaturation = device.currentValue("saturation", true)
    levelAndModeMatch = currentLevel == master.currentLevel && currentColorMode == master.currentColorMode
    if(master.currentColorMode == 'CT'){
        returnValue = levelAndModeMatch && (Math.abs(currentColorTemperature - master.currentColorTemperature)<15)
    }else{
        returnValue = levelAndModeMatch && currentHue == master.currentHue && currentSaturation == master.currentSaturation
    }
    log.debug "Comparing " + device + " and " + master + " equal: " + returnValue
    log.debug "" +  device + ": " + " level: " + currentLevel + " mode: " + currentColorMode + " temperature: " + currentColorTemperature + " hue: " + currentHue + " saturation: " + currentSaturation
    log.debug "" +  master + ": " + " level: " + master.currentLevel + " mode: " + master.currentColorMode + " temperature: " + master.currentColorTemperature + " hue: " + master.currentHue + " saturation: " + master.currentSaturation
    return returnValue
}
def updateDevice(current) {
    current.setLevel(master.currentLevel)     
    if(master.currentColorMode == 'CT'){
        log.debug "new color mode CT"        
        current.setColorTemperature(master.currentColorTemperature)
    }else{
        log.debug "new color mode RGB"
        current.setColor([hue:master.currentHue,saturation:master.currentSaturation,level:master.currentLevel]); 
    }
}