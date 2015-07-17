var getTitleValue, language_id;
function getArrayFromData(_data, _name) {

	var _result = [];
	if (!_data)
		return _result;
	if (!_name)
		return _result;
	for ( var _i = 0; _i < _data.length; _i++) {
		var _d = _data[_i];
		var _val;
		if (_d)
			_val = _d[_name];
		_result.push(_val);
	}
	return _result;

}

function getValuesFromForm(_form) {
	return _form.getValues();
}

function getValueFromForm(_form, _name) {
	return _form.getValue(_name);
}

/***** SMSBroadcast *****/
//
//function startLoading(text){
//    var layer = document.createElement("div");
//    layer.classList.add("sms-loading-layer");
//    layer.innerHTML = text || 'Loading...';
//    document.body.appendChild(layer);
//}
//function stopLoading(){
//    var layers = document.querySelectorAll('.sms-loading-layer');
//    for(var i in layers){
//        if(layers[i] && typeof layers[i].remove == 'function'){
//            layers[i].remove();
//        }
//    }
//}
//
//function smsNumberUploadCallback(err){
//    isc.say(err || 'მოთხოვნა წარმატებით დარეგისტრირდა');
//    isc.IButton.getById('smsInfoRefreshBtn').click();
//    stopLoading();
//}
//function smsBlacklistUploadCallback(err){
//    isc.say(err || 'მოთხოვნა წარმატებით დარეგისტრირდა');
//    stopLoading();
//}
//
//function loadSenderList(formName){
//    //startLoading();
//    jQuery.getJSON('./SMSBroadcast/Senders', function(data){
//        if(!data) return ;
//        var combo = jQuery('form[name='+formName+'] select[name=sender]');
//        for(var i = 0; i < data.length; i++){
//            var option = document.createElement("option");
//            jQuery(option).html(data[i].name);
//            jQuery(option).attr('value', data[i].id);
//            jQuery(combo).append(option);
//        }
//        if(formName == 'upload-numbers-form'){
//            var smsOffText = jQuery('form[name=upload-numbers-form] .sms_off_text');
//            smsOffText.html(' SMS Off 91521 Text: ' + combo.find('option:first').html());
//            combo.change(function(){
//                console.log(combo.find('option:selected').html());
//                smsOffText.html(' SMS Off 91521 Text: ' + combo.find('option:selected').html());
//            });
//        }
//
//        //stopLoading();
//    });
//}
//function countSMS(){
//    var smsOffText = jQuery('form[name=upload-numbers-form] .sms_off_text');
//    var v = jQuery('form[name=upload-numbers-form] textarea[name=sms_raw]').val() + smsOffText.html();
//    var t = isASCII(v) ? 146 : 56;
//    var sms_count = Math.ceil(v.length / t);
//    var char_count = v.length % t;
//
//    var w = jQuery('form[name=upload-numbers-form] div.sms_counter');
//    w.html((t - char_count) + ' / ' + sms_count);
//}
//function isASCII(str) {
//    return /^[\x00-\x7F]*$/.test(str);
//}
//
//function copySms(){
//    var sms = jQuery('form[name=upload-numbers-form] textarea[name=sms_raw]').val();
//    var smsOffText = jQuery('form[name=upload-numbers-form] .sms_off_text').html();
//    jQuery('form[name=upload-numbers-form] input[name=sms]').val(sms + smsOffText);
//}
//function exportBlackList(btn){
//    if(!btn.form.elements.sender.value){
//        say('მიუთითეთ გამომგზავნი');
//        return;
//    }
//    window.open("./SMSBroadcast/ExportBlackList?sender=" + btn.form.elements.sender.value);
//}
/***** /SMSBroadcast *****/