var deviceInfo;
var listenerRunning = false;
var pointList;
var bndwr = null;
var msg = {
        "mod.bacnet.sendingWhoIs": "",
        "mod.bacnet.getDetails": "",
        "mod.bacnet.listenerStopped": "",
        "mod.bacnet.tester.sendObjectList": "",
        "mod.bacnet.addPoint": "",
        "mod.bacnet.device": "",
        "mod.bacnet.objects" : "",
        "dsEdit.saveWarning" : ""
};

function initImpl() {
    whoIsButtons(false);
    hide("deviceListDiv");
    hide("deviceObjectsMessage");
    hide("deviceObjectsDiv");
    hide("objectListDiv");
};

function sendWhoIs() {
    $set("whoIsMessage", msg["mod.bacnet.sendingWhoIs"]);
    dwr.util.removeAllRows("iamsReceived");
    whoIsButtons(true);
    listenerRunning = true;
    sendBACnetWhoIs();
};

function sendWhoIsCB() {
    setTimeout(sendWhoIsUpdate, 1000);
};

function sendWhoIsUpdate() {
    bndwr.bacnetWhoIsUpdate(function(result) {
        if (result)
            $set("whoIsMessage", result.message);
        if (result && !result.finished) {
            show("deviceListDiv");
            dwr.util.addRows("iamsReceived", result.devices, [
                    function(device) { return device.instanceNumber; },
                    function(device) { return device.networkNumber; },
                    function(device) { return device.mac; },
                    function(device) { return device.link; },
                    function(device) {
                            var s = "getDeviceObjects(";
                            s += device.instanceNumber;
                            s += ","+ device.networkNumber;
                            s += ",'"+ device.mac +"'";
                            if (device.link)
                                s += ",'"+ device.link +"'";
                            else
                                s += ",null";
                            s += "); return false;"
                            return writeImage("deviceDetailsImg"+ device.key, null, "control_play_blue",
                                    msg["mod.bacnet.getDetails"], s);
                    }
                ],
                {
                    rowCreator: function(options) {
                        var tr = document.createElement("tr");
                        tr.id = "deviceIndex"+ options.rowData.key;
                        tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                        return tr;
                    }
                }
              );
            
            sendWhoIsCB();
        }
        else {
            whoIsButtons(false);
            dwr.util.removeAllRows("iamsReceived");
        }
    });
};

function cancelWhoIs(callback) {
    bndwr.cancelDiscovery(function() {
        if (callback)
            callback();
        if (listenerRunning) {
            $set("whoIsMessage", msg["mod.bacnet.listenerStopped"]);
            listenerRunning = false;
        }
        hide("deviceListDiv");
        hide("deviceObjectsMessage");
        hide("deviceObjectsDiv");
        whoIsButtons(false);
        
        dwr.util.removeAllRows("iamsReceived");
    });
};

function getDeviceObjects(devId, netNo, mac, link) {
    hide("deviceObjectsDiv");
    show("deviceObjectsMessage");
    $set("deviceObjectsMessage", msg["mod.bacnet.tester.sendObjectList"]);
    bndwr.sendDeviceObjectListRequest(devId, netNo, mac, link, function(result) {
        if (result.data.error)
            $set("deviceObjectsMessage", result.data.error);
        else {
            hide("deviceObjectsMessage");
            drawObjects(result.data, "deviceObjects");
        }
    });
};

function drawObjects(result, listId) {
    deviceInfo = result;
    show(listId +"Div");
    
    $set(listId +"Header", "<b>"+ result.deviceDescription +"</b> ("+ result.deviceName +"/" + result.deviceId +"), "+ 
    		result.deviceObjects.length +" "+ msg["mod.bacnet.objects"]);
    
    dwr.util.removeAllRows(listId);
    dwr.util.addRows(listId, result.deviceObjects, [
                function(obj) { return obj.objectName; },
                function(obj) { return obj.objectTypeDescription; },
                function(obj) { return obj.prettyPresentValue; },
                function(obj, options) {
                	if (!objectTypeIsValid(obj.objectTypeId))
                		return "";
                    return '<a href="#" onclick="addPoint('+ options.rowNum +
                            '); return false">'+ msg["mod.bacnet.addPoint"] +'</a>'; },
                function(obj, options) {
                 	if (!objectTypeIsValid(obj.objectTypeId))
                 		return "";
                    return '<input type="checkbox" name="addObject-'+ listId +'" value="'+ options.rowNum +'"/>';
                }
            ],
            {
                rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                }
            }
    );
};

function whoIsButtons(running) {
    setDisabled("sendWhoIsBtn", running);
    setDisabled("cancelWhoIsBtn", !running);
};

function sendObjListRequest() {
    cancelWhoIs(function() {
        setDisabled("sendObjListBtn", true);
        hide("objectListDiv");
        sendObjectListRequest(function(result) {
            if (result.data.error)
                $set("objListMessage", result.data.error);
            else {
                $set("objListMessage");
                drawObjects(result.data, "objectList");
            }
            
            setDisabled("sendObjListBtn", false);
        });
    });
};

function addPointImpl(objIndex) {
    var obj = deviceInfo.deviceObjects[objIndex];
    delete obj.prettyPresentValue;
    if (deviceInfo.deviceNetwork == null)
        deviceInfo.deviceNetwork = 0;
    bndwr.addBacnetPoint(deviceInfo.deviceNetwork, deviceInfo.deviceMac, deviceInfo.deviceLink, deviceInfo.deviceId,
            obj, editPointCB);
};

function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
    pointListColumnHeaders[pointListColumnHeaders.length] = msg["mod.bacnet.device"];
    pointListColumnFunctions[pointListColumnFunctions.length] = function(p) {
        if (p.pointLocator.networkNumber > 0)
            return p.pointLocator.networkNumber +" @ "+ p.pointLocator.mac;
        return p.pointLocator.mac;
    };
};

function editPointCBImpl(locator) {
    $set("networkNumber", locator.networkNumber);
    $set("mac", locator.mac);
    $set("link", locator.link);
    $set("remoteDeviceInstanceNumber", locator.remoteDeviceInstanceNumber);
    $set("objectTypeId", locator.objectTypeId);
    $set("objectInstanceNumber", locator.objectInstanceNumber);
    $set("useCovSubscription", locator.useCovSubscription);
    $set("settable", locator.settable);
    $set("writePriority", locator.writePriority);
    $set("dataTypeId", locator.dataTypeId);
    objectTypeChanged();
};

function savePointImpl(locator) {
    delete locator.relinquishable;
    
    locator.networkNumber = $get("networkNumber");
    locator.mac = $get("mac");
    locator.link = $get("link");
    locator.remoteDeviceInstanceNumber = $get("remoteDeviceInstanceNumber");
    locator.objectTypeId = $get("objectTypeId");
    locator.objectInstanceNumber = $get("objectInstanceNumber");
    locator.useCovSubscription = $get("useCovSubscription");
    locator.settable = $get("settable");
    locator.writePriority = $get("writePriority");
    locator.dataTypeId = $get("dataTypeId");
    
    saveBACnetPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
};

function objectTypeChanged() {
    if (objectTypeIsSettable($get("objectTypeId")))
        setDisabled("settable", false);
    else {
        setDisabled("settable", true);
        $set("settable", false);
    }
    settableChanged();
};

function settableChanged() {
    setDisabled("writePriority", !$get("settable"));
};

function selectAllObjects(listId) {
	var a = document.getElementsByName("addObject-"+ listId);
	for (var i=0; i<a.length; i++)
		a[i].checked = true;
};

function unselectAllObjects(listId) {
	var a = document.getElementsByName("addObject-"+ listId);
	for (var i=0; i<a.length; i++)
		a[i].checked = false;
};

function addObjectsAsPoints(listId) {
    if (!isShowing("pointProperties")) {
        alert(msg["mod.bacnet.sendingWhoIs"]);
        return;
    }
    
	var a = $get("addObject-"+ listId);
	if (a.length > 0) {
		var objs = [];
		for (var i=0; i<a.length; i++) {
		    var obj = deviceInfo.deviceObjects[a[i]];
		    delete obj.prettyPresentValue;
			objs.push(obj);
		}
		
	    if (deviceInfo.deviceNetwork == null)
	        deviceInfo.deviceNetwork = 0;
	    
	    bndwr.createPointsFromObjects(deviceInfo.deviceNetwork, deviceInfo.deviceMac, deviceInfo.deviceLink, 
	    		deviceInfo.deviceId, objs, writePointList);
	}
};
