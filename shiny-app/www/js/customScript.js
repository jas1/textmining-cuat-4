$(document).ready(function(){
  // inicializar componentes

  $( "#searchButton" ).on( "click", function( event, ui ) {

    clickSearchButton();
  });

  // declarar shiny handlers
  declareShinyHandlers();
});

function clickSearchButton(){
  // valor de la query
  var queryValue = $('#inputQuery').val();
  // div donde muestro que query hice: limpio
  $('#queryInputDiv').html('');
  // div donde muestro que query hice: nuevo valor
  $('#queryInputDiv').append('<h2>QUERY:</h2>').append('<p>'+queryValue+'</p>');

  // llamada a shiny para realizar la query
  Shiny.onInputChange("queryMagica", queryValue);
}

// para declarar los componetes de shiny que voy a andar usando
function declareShinyHandlers(){

  Shiny.addCustomMessageHandler("querySearchFuncHandler", function(queryResponse) {
    if (queryResponse!='') {
      querySearchFuncHandlerFunc(queryResponse);
    }
  });
}


// lo mismo que antes me molestan las anonimas
// ademas encapsulo por si llego a querer hacer otro tipo de implementaciones
// solo tengo que cambiar la llamada de la funcion aca
// @param : la respuesta recibida de shiny , espero un json
function querySearchFuncHandlerFunc(queryResponse){

  var responseDivId = 'queryResponseDiv';
  // limpia anterior
  $('#queryResponseTitle').html("<h3>Se encontro:</h3>");
  $('#'+responseDivId).html("");
  solRJsonResponseHandler(queryResponse,responseDivId);

}

// llamada al parseo de la response de solR
// @param: queryResponse: la respuesta de solR
// @param: responseDivId: el div donde se va a escribir la respuesta parseada
function solRJsonResponseHandler(queryResponse,responseDivId){

  //var  responseTxt = JSON.stringify(queryResponse);
  var  responseTxt = ''
  var objs = jsonPath(queryResponse, "id.*")
  for (var key in objs) {
    var current=objs[key];
    var currentName=current.substring(current.lastIndexOf("/")+1);
    $('#'+responseDivId).append('<p><a target="_blank" href="file://'+current+'">'+currentName+'</a></p>');
  }


  console.log(responseTxt);
  //$('#'+responseDivId).html(responseTxt);

}
