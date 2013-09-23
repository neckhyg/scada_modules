//
// Main
mango.view.initMain = function() {
    mango.view.setPoint = mango.view.main.setPoint;
    
    // Tell the long poll request that we're interested in watchlist data, and register our js handler.
    mango.longPoll.addHandler("watchlist", function(response) {
    	if (response.mainStates)
    	    mango.view.main.setData(response.mainStates);
    });
};

mango.view.main = {};
mango.view.main.reset = function() {
	WatchListDwr.resetWatchListState(mango.longPoll.pollSessionId);
};

mango.view.main.setPoint = function(pointId, componentId, value) {
    startImageFader("p"+ pointId +"Changing");
    mango.view.hideChange("p"+ pointId +"Change");
    WatchListDwr.setPoint(pointId, componentId, value, function(pointId) {
        stopImageFader("p"+ pointId +"Changing");
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};

mango.view.main.setData = function(stateArr) {
    for (var i=0; i<stateArr.length; i++)
        mango.view.main.setDataImpl(stateArr[i]);
};
    
mango.view.main.setDataImpl = function(state) {
    // Check that the point exists. Ignore if it doesn't.
    if (state && $("p"+ state.id)) {
        var node;
        if (state.value != null) {
            node = $("p"+ state.id +"Value");
            node.innerHTML = state.value;
            dojo.addClass(node, "viewChangeBkgd");
            setTimeout('mango.view.main.safeRemoveClass("'+ node.id +'", "viewChangeBkgd")', 2000);
        }
        
        if (state.time != null) {
            node = $("p"+ state.id +"Time");
            node.innerHTML = state.time;
            dojo.addClass(node, "viewChangeBkgd");
            setTimeout('mango.view.main.safeRemoveClass("'+ node.id +'", "viewChangeBkgd")', 2000);
        }
        
        if (state.change != null) {
            show($("p"+ state.id +"ChangeMin"));
            if (!mango.view.setEditing)
                $set("p"+ state.id +"Change", state.change);
        }
        
        if (state.chart != null) {
            show($("p"+ state.id +"ChartMin"));
            $set("p"+ state.id +"Chart", state.chart);
        }
        
        if (state.messages != null)
            $("p"+ state.id +"Messages").innerHTML = state.messages;
        //else
        //    $("p"+ state.id +"Messages").innerHTML = "";
    }
};

mango.view.main.safeRemoveClass = function(nodeId, className) {
    var node = $(nodeId);
    if (node)
        dojo.removeClass(node, className);
};
