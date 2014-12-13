/**
 * Rende della stezza altezza, tile con class same_height_XX che abbiano una tile-text
 */
$( document ).ready(function() {
  var func = function(){
    var i=1;
    var divs = $(' .same_height_'+i);
    //console.log(divs.length);
    while(divs.length!=0){
      var max = divs[0];
      divs.each(function(index){
        if($('#'+max.id).height()<$(this).height()){
          max = this;      
        }
      });
      //console.log('max:'+$('#'+max.id).height());
      divs.each(function(index){
        if(this.id!=max.id){
          //console.log(this.id);
          var tileText = $('#'+this.id+' .tile-text');
          if(tileText.length==1)
              tileText.css('height', tileText.innerHeight()+$(max).innerHeight()-$(this).innerHeight());
        }
      });
    
   divs = $(' .same_height_'+(++i));
    }
  };
  func();
  $(window).resize(function() {
    //console.log('resized');
    func();
  });

});
