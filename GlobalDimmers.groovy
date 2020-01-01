definition(
  name: "Global Dimmers",
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
        input "dimmerDevices", "capability.switchLevel", title: "Dimmers", multiple: true, required: true
        input "masterDimmer", "capability.switchLevel", title: "Master Dimmer", multiple: false, required: true
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
  subscribe(dimmerDevices, "switch.on", handleDeviceEvent)
  subscribe(masterDimmer, "level", handleLevelEvent)
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
    def masterLevel = masterDimmer.currentLevel
    log.debug masterLevel
    def curDevice = evt.getDevice()    
    curDevice.setLevel(masterLevel)
    log.debug "GlobalDimmers: from ${evt.name} setting ${curDevice.displayName} to ${masterLevel}"
}

def handleLevelEvent(evt) {
    log.debug "handleLevelEvent"
    dimmerDevices.each {
        if (it.currentValue("switch") == "on"){
            it.setLevel(masterDimmer.currentLevel)   
            log.debug "GlobalDimmers: setting ${it.displayName} to ${masterDimmer.currentLevel}"
        }
    }    
}