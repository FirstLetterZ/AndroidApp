function CallNative(name, body, callBack) {
    if (!name) {
        return;
    }
    if(callBack && typeof callBack === 'function'){
        var date = new Date();
        var id = date.toISOString();
        var newAction = {
            id: id,
            callBack: callBack,
        };
        Native.actions.push(newAction);
        window.bridge.callNative(name,body,id);
    }else{
        var id = "";
        window.bridge.callNative(name,body,id);
    }
};
function NativeCallBack(id, body) {
    if (!id) {
        return;
    }
    if (!Native.actions) {
        return;
    }
    for (var index in Native.actions) {
        var action = Native.actions[index];
        if (action.id === id) {
            var callBack = action.callBack;
            if (callBack && typeof callBack === 'function') {
                if (body) {
                    callBack(body)
                } else {
                    callBack()
                }
            }
            break;
        }
    }
};
if (!Native) {
    var Native = window.Native={
        actions : [],
        jsCall : CallNative
    };
}
