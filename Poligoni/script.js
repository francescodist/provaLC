$("document").ready(function(){

  //sovrascrittura alert
  alert = function(message) {
    $("body").append("<div>"+message+"</div>");
  }
  
  // METODO CLASSICO CON PROTOTIPI //
  function Poligono(n) {
    var numeroLati = n;
    this.stampaNumeroLati = function(){
      console.log(numeroLati);
    };
  }

  Rettangolo.prototype = new Poligono(4);
  Rettangolo.prototype.constructor = Rettangolo;
  function Rettangolo(){};
  ////////////////////////////////////


  //togliere commento e commentare il precedente metodo prototipi per provare le classi di ES6
  /*
  // METODO ES6 CON SINTASSI ''CLASS'' (Pochi Browser compatibili)//
  class Poligono {
    constructor(numeroLati) {
      this.numeroLati = numeroLati;
    }
    stampaNumeroLati() {
      console.log(this.numeroLati)
    }
  }

  class Rettangolo extends Poligono {
    constructor(){
      super(4);
    }
  }
  ///////////////////////////////////
  */


  poligoni = [];
  //generazione casuale numero poligoni
  var nPoligoni = Math.floor((Math.random() * 50) + 1);
  while(nPoligoni--){
    //scelta casuale tra Rettangolo e Poligono generico
    if(Math.floor(Math.random() * 2)==0){
      poligoni.push(new Rettangolo());
    }
    else {
      //in caso di Poligono generico scelta casuale di numero lati (tra 3 e 50)
      poligoni.push(new Poligono(Math.floor((Math.random() * 48) + 3)));
    }
    //creazione di button con riferimento alla stampa dell'oggetto appena creato
    $("body").append("<button onclick='poligoni["+(poligoni.length-1)+"].stampaNumeroLati()'>Poligono N. "+poligoni.length+"</button>")
  }
})
