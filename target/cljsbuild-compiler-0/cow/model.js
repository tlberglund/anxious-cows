goog.provide('cow');
goog.require('cljs.core');
goog.require('goog.Timer');
goog.require('clojure.browser.dom');
goog.require('clojure.browser.event');
cow.cow_count = 50;
cow.pi = 3.1415926535;
cow.random_cow = (function random_cow(){
var cow__$1 = cljs.core.atom.call(null,cljs.core.ObjMap.fromObject(["\uFDD0'anxiety","\uFDD0'angle","\uFDD0'velocity","\uFDD0'x","\uFDD0'y","\uFDD0'self-differentiation"],{"\uFDD0'anxiety":0,"\uFDD0'angle":((2 * cow.pi) - cljs.core.rand.call(null,(4 * cow.pi))),"\uFDD0'velocity":cljs.core.rand.call(null),"\uFDD0'x":(1 - cljs.core.rand.call(null,2)),"\uFDD0'y":(1 - cljs.core.rand.call(null,2)),"\uFDD0'self-differentiation":cljs.core.rand.call(null)}));
return cow__$1;
});
cow.canvas = clojure.browser.dom.get_element.call(null,"model");
cow.timer = (new goog.Timer((1000 / 60)));
cow.init_simulator = (function init_simulator(count){
var cows = cljs.core.doall.call(null,cljs.core.take.call(null,cow.cow_count,cljs.core.repeatedly.call(null,cow.random_cow)));
return cows;
});
cow.cows = cow.init_simulator.call(null,cow.cow_count);
cow.paint_cow = (function paint_cow(canvas){
var ctx = canvas.getContext("2d");
var width = canvas.getAttribute("width");
var height = canvas.getAttribute("height");
var ctx_x = ((width / 2) + ((width / 2) * (new cljs.core.Keyword("\uFDD0'x")).call(null,cow.cow)));
var ctx_y = ((height / 2) + ((height / 2) * (new cljs.core.Keyword("\uFDD0'y")).call(null,cow.cow)));
ctx.beginPath();
ctx.fillRect(ctx_x,ctx_y,5,5);
return ctx.closePath();
});
cow.paint_sim = (function paint_sim(canvas,cows){
var G__16183 = cljs.core.seq.call(null,cows);
while(true){
if(G__16183)
{var cow__$1 = cljs.core.first.call(null,G__16183);
cow.paint_cow.call(null,canvas,cljs.core.deref.call(null,cow__$1));
{
var G__16184 = cljs.core.next.call(null,G__16183);
G__16183 = G__16184;
continue;
}
} else
{return null;
}
break;
}
});
cow.cow_sim = (function cow_sim(){
return cow.paint_sim.call(null,cow.canvas,cow.cows);
});
clojure.browser.event.listen.call(null,cow.timer,goog.Timer.TICK,cow.cow_sim);
cow.timer.start();
