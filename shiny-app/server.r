if(!require(stringr)) {install.packages("stringr"); require(stringr);}
if(!require(jsonlite)) {install.packages("jsonlite"); require(jsonlite);}

# la idea de esta aplicacion es ahcer de puente rapido para manejar los queries al solR
# recibir un input de un html y devolver los json tuneados o no segun haga falta
# hay que tener el solR levantado
shinyServer(function(input, output,session) {
  
  observe({
    # levanta el input de query magica
    input$queryMagica
    # ejecuta el comando de busqueda
    customMessage <- querySearch( input$queryMagica )
    # devuelve la busqueda
    session$sendCustomMessage(type='querySearchFuncHandler', customMessage) 
  })
})

# para buscar , encapsulo la busqueda real por si despues hago una aplicacion en elastic search u otro motor.
querySearch <- function(queryString){
  tmp <- ''
  if(!is.null(queryString) ){
    tmp <- querySolR(queryString)  
  }
  tmp
}

# solR tiene un json que devuelve y una forma de tirar las queries
querySolR <- function(queryString){
  
  solrUrlJsonHandler <- 'http://localhost:8983/solr/lau_normativa/select?indent=on&wt=json&q='
  ret <- paste(solrUrlJsonHandler,queryString,sep = '')
  jsonRet <- fromJSON(ret)
  jsonRet$response$docs
}