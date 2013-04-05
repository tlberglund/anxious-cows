goog.provide('cow');
goog.require('cljs.core');
goog.require('goog.Timer');
goog.require('clojure.browser.dom');
goog.require('clojure.browser.event');
cow.cow_count = 20;
cow.pi = 3.1415926535;
cow.x_from_polar = (function x_from_polar(theta,radius){
return (radius * Math.cos.call(null,theta));
});
cow.y_from_polar = (function y_from_polar(theta,radius){
return (radius * Math.sin.call(null,theta));
});
cow.random_cow = (function random_cow(){
var theta = ((2 * cow.pi) - cljs.core.rand.call(null,(4 * cow.pi)));
var radius = cljs.core.rand.call(null);
var cow__$1 = cljs.core.atom.call(null,cljs.core.ObjMap.fromObject(["\uFDD0'anxiety","\uFDD0'angle","\uFDD0'velocity","\uFDD0'x","\uFDD0'y","\uFDD0'self-differentiation"],{"\uFDD0'anxiety":0,"\uFDD0'angle":((2 * cow.pi) - cljs.core.rand.call(null,(4 * cow.pi))),"\uFDD0'velocity":cljs.core.rand.call(null,0.01),"\uFDD0'x":cow.x_from_polar.call(null,theta,radius),"\uFDD0'y":cow.y_from_polar.call(null,theta,radius),"\uFDD0'self-differentiation":cljs.core.rand.call(null)}));
return cow__$1;
});
cow.canvas = clojure.browser.dom.get_element.call(null,"model");
cow.timer = (new goog.Timer((1000 / 20)));
cow.cows = cljs.core.doall.call(null,cljs.core.take.call(null,cow.cow_count,cljs.core.repeatedly.call(null,cow.random_cow)));
cow.sim_cows = (function sim_cows(cows){
var G__10673 = cljs.core.seq.call(null,cows);
while(true){
if(G__10673)
{var cow__$1 = cljs.core.first.call(null,G__10673);
var new_x_10674 = ((new cljs.core.Keyword("\uFDD0'x")).call(null,cljs.core.deref.call(null,cow__$1)) + cow.x_from_polar.call(null,(new cljs.core.Keyword("\uFDD0'angle")).call(null,cljs.core.deref.call(null,cow__$1)),(new cljs.core.Keyword("\uFDD0'velocity")).call(null,cljs.core.deref.call(null,cow__$1))));
var new_y_10675 = ((new cljs.core.Keyword("\uFDD0'y")).call(null,cljs.core.deref.call(null,cow__$1)) + cow.y_from_polar.call(null,(new cljs.core.Keyword("\uFDD0'angle")).call(null,cljs.core.deref.call(null,cow__$1)),(new cljs.core.Keyword("\uFDD0'velocity")).call(null,cljs.core.deref.call(null,cow__$1))));
cljs.core.swap_BANG_.call(null,cow__$1,cljs.core.assoc,"\uFDD0'x",new_x_10674,"\uFDD0'y",new_y_10675);
{
var G__10676 = cljs.core.next.call(null,G__10673);
G__10673 = G__10676;
continue;
}
} else
{return null;
}
break;
}
});
cow.init_canvas = (function init_canvas(canvas){
var ctx = canvas.getContext("2d");
var width = canvas.getAttribute("width");
var height = canvas.getAttribute("height");
ctx.clearRect(0,0,width,height);
ctx.beginPath();
ctx.arc((width / 2),(height / 2),(width / 2),0,(2 * cow.pi),false);
return ctx.stroke();
});
cow.paint_cow = (function paint_cow(canvas,cow__$1){
var ctx = canvas.getContext("2d");
var width = canvas.getAttribute("width");
var height = canvas.getAttribute("height");
var ctx_x = ((width / 2) + ((width / 2) * (new cljs.core.Keyword("\uFDD0'x")).call(null,cow__$1)));
var ctx_y = ((height / 2) + ((height / 2) * (new cljs.core.Keyword("\uFDD0'y")).call(null,cow__$1)));
ctx.beginPath();
ctx.fillRect(ctx_x,ctx_y,5,5);
return ctx.closePath();
});
cow.paint_sim = (function paint_sim(canvas,cows){
cow.init_canvas.call(null,canvas);
var G__10678 = cljs.core.seq.call(null,cows);
while(true){
if(G__10678)
{var cow__$1 = cljs.core.first.call(null,G__10678);
cow.paint_cow.call(null,canvas,cljs.core.deref.call(null,cow__$1));
{
var G__10679 = cljs.core.next.call(null,G__10678);
G__10678 = G__10679;
continue;
}
} else
{return null;
}
break;
}
});
cow.cow_sim = (function cow_sim(){
cow.sim_cows.call(null,cow.cows);
return cow.paint_sim.call(null,cow.canvas,cow.cows);
});
clojure.browser.event.listen.call(null,cow.timer,goog.Timer.TICK,cow.cow_sim);
cow.timer.start();
