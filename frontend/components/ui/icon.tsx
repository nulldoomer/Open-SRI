"use client"
import * as React from "react"
import {cva,type VariantProps} from "class-variance-authority"
import {cn} from "@/lib/utils"
import {iconData,type IconNode} from "@/lib/cojeev/icon-data"
import {getLucideIcon,lucideIconNames} from "@/lib/cojeev/lucide-icon-data"
import {useMorph} from "@/lib/cojeev-motion/use-morph"
import {useFlowPress} from "@/lib/cojeev-motion/flow-press"
import {assignMotionRef} from "@/lib/cojeev-motion/refs"
import {createMotionLane, motionTokens, useChoreography} from "@/lib/cojeev-motion/choreography"

// Small authored additions share the pack's 24px stroke geometry.
const extraPaths: Record<string, string[]> = {
  "corner-down-left": ["m9 10-5 5 5 5", "M20 4v7a4 4 0 0 1-4 4H4"],
  github: ["M9 19c-4.3 1.3-4.3-2.2-6-2.7M15 22v-3.8c0-1.1-.4-1.8-.8-2.2 2.7-.3 5.5-1.3 5.5-6A4.7 4.7 0 0 0 18.4 6a4.4 4.4 0 0 0-.1-3.9S17.2 1.7 14.5 3a13 13 0 0 0-7 0C4.8 1.7 3.7 2.1 3.7 2.1A4.4 4.4 0 0 0 3.6 6a4.7 4.7 0 0 0-1.3 3.3c0 4.7 2.8 5.7 5.5 6-.4.4-.8 1.2-.8 2.2V22"],
  save: ["M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h12l4 4v12a2 2 0 0 1-2 2Z", "M7 3v6h9V3M7 21v-8h10v8"],
  "circle-help": ["M9.1 9a3 3 0 0 1 5.8 1c0 2-3 2-3 4M12 17h.01", "M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0"],
  menu: ["M4 6h16M4 12h16M4 18h16"],
  mail: ["M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z", "m22 6-10 7L2 6"],
  link: ["M10 13a5 5 0 0 0 7 .3l3-3a5 5 0 0 0-7-7l-1.8 1.8M14 11a5 5 0 0 0-7-.3l-3 3a5 5 0 0 0 7 7l1.8-1.8"],
  code: ["m8 5-7 7 7 7m8-14 7 7-7 7m-3-16-2 18"],
  compass: ["M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0", "m16 8-3 5-5 3 3-5 5-3Z"],
  "folder-plus": ["M3 7V5a2 2 0 0 1 2-2h4l2 3h8a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7Z", "M12 10v7m-3-3.5h6"],
  "undo-2": ["M3 10h11a6 6 0 0 1 0 12M7 6l-4 4 4 4"],
  "redo-2": ["M21 10H10a6 6 0 0 0 0 12M17 6l4 4-4 4"],
  globe: ["M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0M2 12h20M12 2c5 5.5 5 14.5 0 20-5-5.5-5-14.5 0-20Z"],
  "log-in": ["M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M3 12h12m-4-4 4 4-4 4"],
  filter: ["M3 4h18l-7 8v7l-4 2v-9L3 4Z"],
  palette: ["M12 3a9 9 0 0 0 0 18h1a2 2 0 0 0 1.4-3.4 1 1 0 0 1 .7-1.6H17a4 4 0 0 0 4-4c0-5-4-9-9-9Z", "M7.5 9h.01M10 6h.01M15 6.5h.01M17.5 10h.01"],
  rocket: ["M12 15c-3 0-6-3-6-3C7 6 12 2 21 3c1 9-3 14-9 15l-3-3M6 12H3l2-5h3M12 18v3l5-2v-3", "m4 16-2 6 6-2M15 8h.01"],
  "arrow-up": ["M12 19V5m-7 7 7-7 7 7"],
  paperclip: ["m21.4 11.6-9.2 9.2a6 6 0 0 1-8.5-8.5l10-10a4 4 0 0 1 5.7 5.7l-10 10a2 2 0 0 1-2.8-2.8l9.2-9.2"],
  square: ["M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z"],
  "shield-check": ["M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z", "m8 12 3 3 5-6"],
};
// Separate authored compound strokes into owned parts; coordinates and silhouettes are unchanged.
for(const name of ["menu","globe","link","save","rocket","circle-help"]) {
  extraPaths[name]=extraPaths[name].flatMap(d=>d.match(/[Mm][^M]*/g)??[d]);
}
extraPaths.code=["m8 5-7 7 7 7","m16 5 7 7-7 7","m13 3-2 18"];
const additionalIcons = Object.fromEntries(Object.entries(extraPaths).map(([name, paths]) => [name, paths.map(d => ({ tag: "path", attrs: { d }, children: [] }))])) as Record<string, IconNode[]>;
// A small authored set for the gallery's most-recognized marks. These are line silhouettes,
// not a decorative plate; the broad Lucide pack keeps its original geometry where a safe contour
// conversion cannot be inferred.
const organicIconGeometry:Record<string,IconNode[]>={
  camera:[{tag:"path",attrs:{d:"M4 7.6 Q5 7 7 7.4 L8.3 5.2 Q9.1 4 10.3 4.2 L13.8 4.1 Q15.1 4 15.8 5.3 L16.7 7.2 Q18 7 20 7.8 Q21.3 8.6 21 10.1 L21 17.8 Q20.8 20 18.8 20 L5.1 20 Q3 19.8 3 17.8 L3 10 Q3 8.3 4 7.6"},children:[]},{tag:"path",attrs:{d:"M9 13 Q9 9.8 12.1 9.8 Q15.2 10 15.1 13 Q15 16.1 12 16.2 Q9 16 9 13"},children:[]}],
  heart:[{tag:"path",attrs:{d:"M20.4 8.4 Q20.2 4.4 16.7 4.2 Q13.8 4.1 12 6.8 Q10.1 4 7.3 4.3 Q3.7 4.7 3.6 8.5 Q3.7 12.2 12 20 Q20.3 12.4 20.4 8.4"},children:[]}],
  leaf:[{tag:"path",attrs:{d:"M20 4 Q11.8 3.7 7.4 8 Q3.8 11.6 4.1 17.6 Q10.2 18 14 14.2 Q18.2 10.2 20 4"},children:[]},{tag:"path",attrs:{d:"M4.5 19.5 Q9.5 14.2 15.8 8.7"},children:[]}],
  search:[{tag:"path",attrs:{d:"M18.5 10.8 Q18.5 3.8 11.2 3.6 Q4 3.8 3.7 10.8 Q3.8 17.8 10.8 18 Q14.2 18 16.4 15.8"},children:[]},{tag:"path",attrs:{d:"M16.1 16.1 Q18.7 18.6 21 21"},children:[]}],
  check:[{tag:"path",attrs:{d:"M4.8 12.2 Q7.2 14.8 9.8 17 Q14.8 11 19.8 6"},children:[]}],
  "arrow-right":[{tag:"path",attrs:{d:"M4 12 Q11.8 11.5 20 12"},children:[]},{tag:"path",attrs:{d:"M14 6 Q17.1 8.9 20 12 Q17.1 15.1 14 18"},children:[]}],
};

// Friendly action names reuse existing geometry; legacy Lucide names remain valid.
const iconAliases: Record<string, string> = {
  "check-circle":"circle-check", close:"x", alert:"triangle-alert",
  loader:"loader-circle", refresh:"refresh-cw", trash:"trash-2",
  home:"house", "help-circle":"circle-help", "plus-circle":"circle-plus", "more-horizontal":"ellipsis",
};
const canonicalIcon = (name:string) => iconAliases[name] ?? name;
// Preserve authored part ordering: existing motion recipes depend on these silhouettes.
const geometryCache=new Map<string,IconNode[]>();
function iconGeometry(name:string):IconNode[]|undefined {
  name=canonicalIcon(name);
  const known=iconData[name]??additionalIcons[name]??geometryCache.get(name);
  if(known)return known;
  const nodes=getLucideIcon(name);
  if(nodes)geometryCache.set(name,nodes);
  return nodes;
}

/** Direction is geometric, independent of document reading direction. */
export function getIconDirection(name:string): readonly [number, number] {
  name=canonicalIcon(name);
  if(name==="send"||name==="arrow-ur"||name==="external-link")return [1,-1];
  if(name==="download")return [0,1];
  if(name==="upload")return [0,-1];
  if(name==="arrow-r")return [1,0];
  if(name==="arrow-l")return [-1,0];
  const x=name.includes("left")?-1:name.includes("right")?1:0;
  const y=name.includes("up")?-1:name.includes("down")?1:0;
  return [x,y];
}
type IconPartMotion = {
  parts:"glyph"|number[];
  move?:[number,number]; scale?:[number,number]; pivot?:[number,number];
  rotate?:number; turn?:boolean; trace?:boolean; opacity?:number; delay?:number; waves?:number;
};
type IconRecipe = {description:string;motions:IconPartMotion[]};
// A compact authored vocabulary: travel, hinge, stretch, trace, and emphasis.
const iconRecipes:Record<string,IconRecipe> = {
  "party-popper":{"description":"Confetti spreads from the cone while its rim stays anchored.","motions":[{"parts":[1,7],"move":[-0.7,-1.5]},{"parts":[2,3,5],"move":[1,-1.5]},{"parts":[4,6],"move":[1.5,0.4]}]},
  "arrow-right":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[2.5,0.0]}]},
  "arrow-left":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[-2.5,0.0]}]},
  "arrow-up":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[0.0,-2.5]}]},
  "arrow-down":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[0.0,2.5]}]},
  "chevron-up":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[0.0,-2.5]}]},
  "chevron-down":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[0.0,2.5]}]},
  "chevron-left":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[-2.5,0.0]}]},
  "chevron-right":{"description":"The glyph travels in its drawn direction and returns.","motions":[{"parts":"glyph","move":[2.5,0.0]}]},
  "external-link":{"description":"The arrow exits upward-right while the window frame stays fixed.","motions":[{"parts":[0,1],"move":[1.8,-1.8]}]},
  "play":{"description":"The play triangle presses inward, then returns to its start position.","motions":[{"parts":"glyph","move":[1.4,0],"scale":[-0.07,-0.07]}]},
  "pause":{"description":"The two pause bars draw inward toward a held center.","motions":[{"parts":[0],"move":[-1.2,0]},{"parts":[1],"move":[1.2,0]}]},
  "skip-forward":{"description":"The triangle advances toward its stationary stop bar.","motions":[{"parts":[1],"move":[2,0]}]},
  "volume-2":{"description":"Sound waves expand outward from the stationary speaker.","motions":[{"parts":[1],"move":[1,0],"scale":[0,0.12],"pivot":[16,12]},{"parts":[2],"move":[1.5,0],"scale":[0,0.1],"pivot":[19,12],"delay":0.12}]},
  "volume-x":{"description":"The mute cross traces over a stationary speaker.","motions":[{"parts":[1],"trace":true},{"parts":[2],"trace":true,"delay":0.12}]},
  "mic":{"description":"The microphone capsule responds to a short voice beat above its fixed stand.","motions":[{"parts":[2],"scale":[0,-0.13],"pivot":[12,15],"waves":3}]},
  "video":{"description":"The lens wedge opens from the camera body.","motions":[{"parts":[0],"rotate":-9,"pivot":[16,12]}]},
  "camera":{"description":"The camera lens closes and reopens like a shutter.","motions":[{"parts":[1],"scale":[0,-0.82],"pivot":[12,13]}]},
  "image":{"description":"The landscape traces inside the fixed picture frame as its sun rises.","motions":[{"parts":[2],"trace":true},{"parts":[1],"move":[0,-1]}]},
  "menu":{"description":"Menu strokes slide outward in a short top-to-bottom sequence.","motions":[{"parts":[0],"move":[-1.5,0]},{"parts":[1],"move":[-1.5,0],"delay":0.09},{"parts":[2],"move":[-1.5,0],"delay":0.18}]},
  "ellipsis":{"description":"The three continuation dots lift left to right.","motions":[{"parts":[2],"move":[0,-1.5]},{"parts":[0],"move":[0,-1.5],"delay":0.1},{"parts":[1],"move":[0,-1.5],"delay":0.2}]},
  "maximize":{"description":"The four window corners expand outward and return.","motions":[{"parts":[0],"move":[-1,-1]},{"parts":[1],"move":[1,-1]},{"parts":[2],"move":[-1,1]},{"parts":[3],"move":[1,1]}]},
  "layout-grid":{"description":"Dashboard tiles settle into place in a clockwise sequence.","motions":[{"parts":[0],"scale":[-0.12,-0.12],"pivot":[6.5,6.5],"delay":0.0},{"parts":[1],"scale":[-0.12,-0.12],"pivot":[17.5,6.5],"delay":0.08},{"parts":[2],"scale":[-0.12,-0.12],"pivot":[17.5,17.5],"delay":0.16},{"parts":[3],"scale":[-0.12,-0.12],"pivot":[6.5,17.5],"delay":0.24}]},
  "pencil":{"description":"The pencil writes a small stroke around its tip.","motions":[{"parts":"glyph","rotate":7,"pivot":[3,21],"waves":3}]},
  "share-2":{"description":"Connections trace toward the two receiving nodes.","motions":[{"parts":[3],"trace":true},{"parts":[0],"scale":[0.13,0.13],"pivot":[18,5],"delay":0.12},{"parts":[2],"scale":[0.13,0.13],"pivot":[18,19],"delay":0.2}]},
  "filter":{"description":"The funnel compresses toward its narrow outlet.","motions":[{"parts":"glyph","scale":[-0.09,-0.08],"pivot":[12,20]}]},
  "eye":{"description":"The eyelid closes briefly over a pupil that refocuses.","motions":[{"parts":[0],"scale":[0,-0.7]},{"parts":[1],"scale":[0,-0.7]}]},
  "eye-off":{"description":"The diagonal concealment stroke traces across the stationary eye.","motions":[{"parts":[3],"trace":true}]},
  "rocket":{"description":"The rocket travels along its nose direction while its exhaust extends.","motions":[{"parts":"glyph","move":[1.4,-1.4]},{"parts":[3],"scale":[0.16,0.16],"pivot":[4,20]}]},
  "save":{"description":"The disk label writes from left to right inside the stationary case.","motions":[{"parts":[2],"trace":true}]},
  "link":{"description":"The two chain links draw apart along their axis and reconnect.","motions":[{"parts":[0],"move":[1,-1]},{"parts":[1],"move":[-1,1]}]},
  "bookmark":{"description":"The bookmark slides down into its reading position.","motions":[{"parts":"glyph","move":[0,1.5]}]},
  "lock":{"description":"The shackle lifts above the fixed lock body and closes.","motions":[{"parts":[1],"move":[0,-1.2]}]},
  "key":{"description":"The key turns around its shaft tip, then returns.","motions":[{"parts":"glyph","rotate":15,"pivot":[21,2]}]},
  "log-out":{"description":"The arrow leaves through the stationary doorway.","motions":[{"parts":[0,1],"move":[2,0]}]},
  "mail":{"description":"The envelope flap lifts from its hinge above a fixed body.","motions":[{"parts":[1],"scale":[0,-0.4],"pivot":[12,6]}]},
  "message-circle":{"description":"The message bubble grows gently from its anchored tail.","motions":[{"parts":"glyph","scale":[0.08,0.08],"pivot":[3,21]}]},
  "phone":{"description":"The receiver rings with a damped turn around its center.","motions":[{"parts":"glyph","rotate":9,"waves":5}]},
  "at-sign":{"description":"The mention tail traces around its stationary center.","motions":[{"parts":[1],"trace":true}]},
  "inbox":{"description":"The inbox lip dips as an item arrives, with the outer tray fixed.","motions":[{"parts":[0],"move":[0,0.8]}]},
  "shield":{"description":"The shield braces wider around its guarded center.","motions":[{"parts":"glyph","scale":[0.07,-0.03]}]},
  "circle-help":{"description":"The question mark tilts above its dot while the enclosing ring stays fixed.","motions":[{"parts":[0],"rotate":-10,"pivot":[12,14]}]},
  "circle-plus":{"description":"The plus strokes trace inside the stationary ring.","motions":[{"parts":[1],"trace":true},{"parts":[2],"trace":true,"delay":0.12}]},
  "x-circle":{"description":"The dismissal cross traces inside the stationary ring.","motions":[{"parts":[1],"trace":true},{"parts":[2],"trace":true,"delay":0.12}]},
  "calendar":{"description":"The binder rings press into the stationary calendar page.","motions":[{"parts":[0],"move":[0,1]},{"parts":[1],"move":[0,1],"delay":0.12}]},
  "clock":{"description":"The hands complete one sweep inside the stationary clock face.","motions":[{"parts":[1],"rotate":360,"turn":true}]},
  "timer":{"description":"The start button dips, then the hand sweeps inside the fixed dial.","motions":[{"parts":[0],"move":[0,0.7]},{"parts":[1],"rotate":360,"turn":true,"pivot":[12,14]}]},
  "house":{"description":"The doorway opens around its left hinge inside the fixed house.","motions":[{"parts":[0],"scale":[-0.25,0],"pivot":[9,21]}]},
  "folder":{"description":"The folder tips open around its bottom edge and settles.","motions":[{"parts":"glyph","scale":[0,-0.1],"pivot":[12,20]}]},
  "file-text":{"description":"Text strokes write line by line inside the fixed document.","motions":[{"parts":[2],"trace":true},{"parts":[3],"trace":true,"delay":0.1},{"parts":[4],"trace":true,"delay":0.2}]},
  "code":{"description":"The brackets separate around a stationary slash.","motions":[{"parts":[0],"move":[-1.4,0]},{"parts":[1],"move":[1.4,0]}]},
  "target":{"description":"The bullseye contracts toward its exact center within stationary outer rings.","motions":[{"parts":[2],"scale":[-0.3,-0.3]}]},
  "terminal":{"description":"The prompt advances as the cursor briefly dims and returns.","motions":[{"parts":[1],"move":[1,0]},{"parts":[0],"opacity":-0.65}]},
  "database":{"description":"The top disk compresses as a write settles through the data stack.","motions":[{"parts":[0],"scale":[0,-0.16],"pivot":[12,5]},{"parts":[2],"move":[0,0.8],"delay":0.14}]},
  "cloud":{"description":"The cloud drifts upward and returns without rotating.","motions":[{"parts":"glyph","move":[0.5,-1.4]}]},
  "globe":{"description":"The meridian turns across a stationary globe outline and equator.","motions":[{"parts":[2],"scale":[-0.75,0]}]},
  "monitor":{"description":"The display compresses and reopens above its stationary stand.","motions":[{"parts":[0],"scale":[0,-0.28],"pivot":[12,10]}]},
  "smartphone":{"description":"The phone gives a small lateral notification vibration.","motions":[{"parts":"glyph","move":[0.75,0],"waves":5}]},
  "sun":{"description":"The rays fan outward from the stationary sun core.","motions":[{"parts":[1],"move":[0,-0.8],"delay":0.025},{"parts":[2],"move":[0,0.8],"delay":0.05},{"parts":[3],"move":[-0.6,-0.6],"delay":0.07500000000000001},{"parts":[4],"move":[0.6,0.6],"delay":0.1},{"parts":[5],"move":[-0.8,0],"delay":0.125},{"parts":[6],"move":[0.8,0],"delay":0.15000000000000002},{"parts":[7],"move":[-0.6,0.6],"delay":0.17500000000000002},{"parts":[8],"move":[0.6,-0.6],"delay":0.2}]},
  "moon":{"description":"The crescent rocks gently around its lower tip.","motions":[{"parts":"glyph","rotate":-7,"pivot":[7,19]}]},
  "shopping-cart":{"description":"The cart rolls forward, with its wheels settling at the end.","motions":[{"parts":"glyph","move":[1.6,0]},{"parts":[0],"move":[0,-0.5],"delay":0.18},{"parts":[1],"move":[0,-0.5],"delay":0.24}]},
  "credit-card":{"description":"The payment stripe traces across a stationary card.","motions":[{"parts":[1],"trace":true}]},
  "crown":{"description":"The crown descends toward its stationary lower band.","motions":[{"parts":[0],"move":[0,1.2]}]},
  "dollar-sign":{"description":"The currency stroke traces around the stationary vertical bar.","motions":[{"parts":[1],"trace":true}]},
  "tag":{"description":"The tag swings from its punched hole.","motions":[{"parts":"glyph","rotate":12,"pivot":[7.5,7.5]}]},
  "package":{"description":"The tape seam draws across the closed package.","motions":[{"parts":[3],"trace":true}]},
  "gem":{"description":"Interior facets catch light in sequence inside the fixed stone outline.","motions":[{"parts":[0],"opacity":-0.55},{"parts":[2],"opacity":-0.55,"delay":0.16}]},
  "gift":{"description":"The lid and bow lift together above a stationary gift box.","motions":[{"parts":[2,3],"move":[0,-1.5]}]},
  "wallet":{"description":"The wallet expands slightly from its spine as it fills.","motions":[{"parts":"glyph","scale":[0.07,0],"pivot":[3,12]}]},
  "award":{"description":"The ribbon unfurls beneath a stationary medal.","motions":[{"parts":[0],"scale":[0,-0.25],"pivot":[12,13]}]},
  "bar-chart-3":{"description":"Bars grow from a fixed baseline in a left-to-right sequence.","motions":[{"parts":[1],"scale":[0,-0.25],"pivot":[7,16]},{"parts":[2],"scale":[0,-0.25],"pivot":[12,16],"delay":0.1},{"parts":[3],"scale":[0,-0.25],"pivot":[17,16],"delay":0.2}]},
  "trending-down":{"description":"The descending trend traces toward its lower-right arrowhead.","motions":[{"parts":[1],"trace":true},{"parts":[0],"move":[0.7,0.7],"delay":0.12}]},
  "trending-up":{"description":"The rising trend traces toward its upper-right arrowhead.","motions":[{"parts":[1],"trace":true},{"parts":[0],"move":[0.7,-0.7],"delay":0.12}]},
  "trophy":{"description":"The cup and handles lift above the trophy base.","motions":[{"parts":[2,4,5],"move":[0,-1.1]}]},
  "user":{"description":"The user nods with a small head dip above fixed shoulders.","motions":[{"parts":[1],"move":[0,0.8]}]},
  "users":{"description":"The second figure steps outward from the fixed foreground figure.","motions":[{"parts":[1,2],"move":[1,0]}]},
  "user-plus":{"description":"The add-member cross traces beside a stationary person.","motions":[{"parts":[2],"trace":true},{"parts":[3],"trace":true,"delay":0.12}]},
  "sparkles":{"description":"The accent sparkles twinkle around the main star in sequence.","motions":[{"parts":[1,2],"scale":[0.2,0.2],"pivot":[20,4]},{"parts":[3],"scale":[0.18,0.18],"pivot":[4,20],"delay":0.18}]},
  "zap":{"description":"The bolt traces quickly along its zigzag, then holds its complete outline.","motions":[{"parts":[0],"trace":true}]},
  "flame":{"description":"The flame stretches from its anchored base in a restrained heat pulse.","motions":[{"parts":"glyph","scale":[-0.04,0.1],"pivot":[12,22],"waves":3}]}
};
function recipeFrame(recipe:IconRecipe,p:number,amount:number):IconMotionFrame {
  const frame:IconMotionFrame={};
  for(const motion of recipe.motions) {
    const delay=motion.delay??0;
    const t=Math.max(0,Math.min(1,(p-delay)/(1-delay)));
    const pulse=(motion.waves?Math.sin(Math.PI*t*motion.waves)*(1-t):Math.sin(Math.PI*t))*amount;
    const [cx,cy]=motion.pivot??[12,12];
    const transform:string[]=[];
    if(motion.move)transform.push(`translate(${motion.move[0]*pulse} ${motion.move[1]*pulse})`);
    if(motion.rotate)transform.push(`rotate(${motion.rotate*(motion.turn?t:pulse)} ${cx} ${cy})`);
    if(motion.scale)transform.push(`translate(${cx} ${cy}) scale(${Math.max(.08,1+motion.scale[0]*pulse)} ${Math.max(.08,1+motion.scale[1]*pulse)}) translate(${-cx} ${-cy})`);
    for(const part of motion.parts==="glyph"?["glyph"]:motion.parts) {
      frame[part]={...(transform.length?{transform:transform.join(" ")}:{ }),...(motion.trace?{draw:Math.min(1,t*1.8)}:{}),...(motion.opacity!==undefined?{opacity:Math.max(.15,Math.min(1,1+motion.opacity*pulse))}:{})};
    }
  }
  return frame;
}

function hasIconMotion(name:string) { return !!iconGeometry(name); }
type MotionFamily="direction"|"organic"|"communication"|"pulse"|"trace";
function motionFamily(name:string):MotionFamily {
  if(/^(arrow|chevron|move|corner|undo|redo|send|navigation)/.test(name)&&!/(up-down|left-right)/.test(name))return "direction";
  if(/^(leaf|flower|sprout|tree|feather|wind|cloud|bird|fish|flame|wheat|rainbow)/.test(name))return "organic";
  if(/^(message|mail|speech|contact|inbox)/.test(name))return "communication";
  if(/^(heart|audio|volume|radio|wifi|signal|radar|sun|sparkle)/.test(name))return "pulse";
  return "trace";
}
/** Families extend coverage without guessing which upstream path is a hinge or moving part. */
function familyRecipe(name:string):IconRecipe {
  const family=motionFamily(name);
  if(family==="direction"){
    const [x,y]=getIconDirection(name);
    if(x||y)return {description:"Travels in its drawn direction, then settles back.",motions:[{parts:"glyph",move:[2*x,2*y]}]};
  }
  if(family==="organic")return {description:"Sways gently from its base and settles, like a living stem.",motions:[{parts:"glyph",rotate:5,pivot:[12,21],scale:[-.025,.04]}]};
  if(family==="communication")return {description:"Opens gently from the lower edge, then settles into place.",motions:[{parts:"glyph",scale:[.07,.1],pivot:[6,20]}]};
  if(family==="pulse")return {description:"Gives a short, damped signal pulse and returns to rest.",motions:[{parts:"glyph",scale:[.11,.11],waves:3}]};
  return {description:"Strokes draw in a short sequence, then hold their complete silhouette.",motions:(iconGeometry(name)??[]).map((_,index)=>({parts:[index],trace:true,delay:Math.min(index*.035,.24)}))};
}
export type IconMotionFrame = Record<string,{transform?:string;draw?:number;opacity?:number}>;
/** Original motion in the pack's 24-unit coordinate space. Keys identify owned SVG groups. */
export function getIconMotionFrame(name:string,progress:number,amplitude=1):IconMotionFrame {
  name=canonicalIcon(name);
  const p=Number.isFinite(progress)?Math.max(0,Math.min(1,progress)):1;
  const amount=Number.isFinite(amplitude)?Math.max(0,Math.min(3,amplitude)):1;
  if(!amount)return {};
  if(iconRecipes[name])return recipeFrame(iconRecipes[name],p,amount);
  const pulse=Math.sin(Math.PI*p)*amount;
  const scale=(value:number)=>`translate(12 12) scale(${value}) translate(-12 -12)`;
  const move=(x:number,y:number)=>({transform:`translate(${x} ${y})`});
  switch(name) {
    case "check": return {0:{draw:Math.min(1,p*1.8)}};
    case "circle-check": return {1:{draw:Math.min(1,p*1.8)}};
    case "circle-check-big": return {1:{draw:Math.min(1,p*1.8)}};
    case "activity": return {0:{draw:Math.min(1,p*1.5)}};
    case "x": return {glyph:{transform:scale(1-.12*pulse)}};
    case "alert-triangle": case "alert-circle": return {1:move(0,-1.2*pulse)};
    case "triangle-alert": case "circle-alert":
      return {1:move(0,-1.2*pulse),2:move(0,-.5*pulse)};
    case "info": return {2:move(0,-1.5*pulse)};
    case "loader-circle": case "refresh-cw": return {glyph:{transform:`rotate(${360*p} 12 12)`}};
    case "search": return {1:{transform:`translate(11 11) scale(${1+.07*pulse}) translate(-11 -11)`}};
    case "bell": {
      const swing=Math.sin(4*Math.PI*p)*(1-p)*amount;
      return {0:move(-1.8*swing,0),1:{transform:`rotate(${12*swing} 12 3)`}};
    }
    case "download": return {0:move(0,2*pulse),2:move(0,2*pulse)};
    case "upload": return {0:move(0,-2*pulse),1:move(0,-2*pulse)};
    case "copy": return {0:move(1.2*pulse,1.2*pulse)};
    case "settings": return {0:{transform:`rotate(${60*p} 12 12)`}};
    case "trash-2": {
      const lid={transform:`translate(0 ${-1.5*pulse}) rotate(${-12*pulse} 6 6)`};
      return {3:lid,4:lid};
    }
    case "plus": return {glyph:{transform:`rotate(${90*p} 12 12)`}};
    case "heart": {
      const beat=Math.pow(Math.sin(2*Math.PI*p),2)*Math.sin(Math.PI*p)*amount;
      return {glyph:{transform:scale(1+.13*beat)}};
    }
    case "star": return {glyph:{transform:scale(1+.12*pulse)}};
    case "thumbs-up": return {0:move(0,-1.4*pulse)};
    default:return recipeFrame(familyRecipe(name),p,amount);
  }
}

/** The painter owns only generated groups, never the consumer's SVG transforms or styles. */
export function createIconMotionPainter(svg:SVGSVGElement,name:string,amplitude=1,frames?:(progress:number)=>IconMotionFrame) {
  const groups=new Map(Array.from(svg.querySelectorAll<SVGGElement>("[data-icon-part]")).map(group=>[group.dataset.iconPart!,group]));
  const backdrop=svg.querySelector("[data-icon-backdrop]");
  const written=new Map<Element,Map<string,{before:string|null;value:string}>>();
  const lengths=new Map<SVGGeometryElement,number>();
  const set=(node:Element,attribute:string,value:string)=>{
    let attributes=written.get(node);
    if(!attributes){attributes=new Map();written.set(node,attributes)}
    const previous=attributes.get(attribute);
    // Yield this attribute if a consumer takes ownership during an animation.
    if(previous&&node.getAttribute(attribute)!==previous.value)return;
    attributes.set(attribute,{before:previous?previous.before:node.getAttribute(attribute),value});
    node.setAttribute(attribute,value);
  };
  return {
    paint(progress:number) {
      for(const path of svg.querySelectorAll<SVGPathElement>("path[data-icon-organic-line]")){
        const source=path.dataset.iconOrganicSource;
        if(source){const pulse=Math.sin(Math.PI*Math.max(0,Math.min(1,progress)))*Math.max(0,Math.min(3,amplitude));set(path,"d",organicMotionPath(source,pulse*.18));}
      }
      if(backdrop){
        const p=Number.isFinite(progress)?Math.max(0,Math.min(1,progress)):1;
        const pulse=Math.sin(Math.PI*p)*(Number.isFinite(amplitude)?Math.max(0,Math.min(3,amplitude)):1);
        set(backdrop,"transform",`translate(12 12) rotate(${4*pulse}) scale(${1+.035*pulse} ${1-.025*pulse}) translate(-12 -12)`);
      }
      for(const [part,frame] of Object.entries(frames?frames(progress):getIconMotionFrame(name,progress,amplitude))) {
        const group=groups.get(part);if(!group)continue;
        if(frame.transform)set(group,"transform",frame.transform);
        if(frame.opacity!==undefined)set(group,"opacity",String(frame.opacity));
        if(frame.draw!==undefined)for(const path of group.querySelectorAll<SVGGeometryElement>("path,line,polyline,polygon,rect,circle,ellipse")) {
          const length=lengths.get(path)??path.getTotalLength();lengths.set(path,length);
          set(path,"stroke-dasharray",`${length} ${length}`);
          set(path,"stroke-dashoffset",String(length*(1-frame.draw)));
        }
      }
    },
    restore() {
      for(const [node,attributes] of written)for(const [attribute,{before,value}] of attributes) {
        if(node.getAttribute(attribute)!==value)continue;
        if(before===null)node.removeAttribute(attribute);else node.setAttribute(attribute,before);
      }
      written.clear();
    },
  };
}

function organicPath(d:string,bend:number){
  const horizontal=/^M\s*([\d.-]+)\s+([\d.-]+)h\s*([\d.-]+)$/.exec(d);
  if(horizontal){const [,x,y,width]=horizontal;const end=Number(x)+Number(width);return `M${x} ${y} Q${(Number(x)+end)/2} ${(Number(y)-bend).toFixed(3)} ${end} ${y}`}
  const vertical=/^M\s*([\d.-]+)\s+([\d.-]+)v\s*([\d.-]+)$/.exec(d);
  if(vertical){const [,x,y,height]=vertical;const end=Number(y)+Number(height);return `M${x} ${y} Q${(Number(x)+bend).toFixed(3)} ${(Number(y)+end)/2} ${x} ${end}`}
  return d;
}
function organicMotionPath(d:string,amount:number){
  if(!amount)return d;let index=0;
  return d.replace(/-?\d*\.?\d+/g,value=>{index++;const delta=index%5===0?amount:index%7===0?-amount*.6:0;return delta?(Number(value)+delta).toFixed(3).replace(/\.?(?:0+)$/," ").trim():value});
}
function renderNode(node:IconNode,key:number,organic=false):React.ReactNode{
  const attrs={...node.attrs,key};
  if(organic&&node.tag==="path"&&typeof node.attrs.d==="string")return React.createElement("path",{...attrs,d:organicPath(node.attrs.d,.32),"data-icon-organic-line":"","data-icon-organic-rest":organicPath(node.attrs.d,.32),"data-icon-organic-source":node.attrs.d},...node.children.map((child,index)=>renderNode(child,index,organic)));
  return React.createElement(node.tag,attrs,...node.children.map((child,index)=>renderNode(child,index,organic)))
}
export type IconProps=React.ComponentProps<"svg"> & {
  name:string;
  size?:"default"|"sm"|"lg";
  draw?:boolean;
  /** One-shot feedback from the nearest control. Disable when another owner animates this icon. */
  feedback?:boolean;
  /** Optional Cojeev treatments; outline preserves the original glyph footprint. */
  treatment?:"outline"|"duotone"|"organic";
  /** Accent layer color. Outline remains currentColor for readable action icons. */
  tone?:"current"|"pink"|"blue"|"olive"|"yellow";
  /** Bounded timing for native control feedback; AnimatedIcon adds explicit preset motion. */
  feedbackDuration?:number;
  feedbackEase?:"gentle"|"settle"|"linear"|readonly [number,number,number,number];
}

const iconControlSelector = 'button,a[href],summary,[role="button"],[role="menuitem"],[role="menuitemcheckbox"],[role="menuitemradio"],[role="option"],[role="tab"],[role="checkbox"],[role="radio"],[role="switch"],label';

function useIconFeedback(host:React.RefObject<SVGSVGElement|null>, enabled:boolean, name:string,duration?:number,ease:IconProps["feedbackEase"]="gentle") {
  React.useEffect(() => {
    const svg=host.current;
    const control=svg?.closest<HTMLElement>(iconControlSelector);
    if(!enabled||!svg||!control)return;
    const semantic=hasIconMotion(name)?createIconMotionPainter(svg,name):null;
    let inView=false;
    let active=false;
    let kind:"hover"|"focus"|"press"="hover";
    let baseScale="none",baseRotate="none";
    let pendingKeyClick=false;
    let keyRelease:ReturnType<typeof setTimeout>|undefined;
    const original=new Map<string,{value:string;priority:string}>();
    const written=new Map<string,{value:string;priority:string}>();
    const owns=(property:string,value:{value:string;priority:string})=>svg.style.getPropertyValue(property)===value.value&&svg.style.getPropertyPriority(property)===value.priority;
    const paint=(property:string,value:string)=>{
      if(!original.has(property))original.set(property,{value:svg.style.getPropertyValue(property),priority:svg.style.getPropertyPriority(property)});
      svg.style.setProperty(property,value);written.set(property,{value:svg.style.getPropertyValue(property),priority:svg.style.getPropertyPriority(property)});
    };
    const restore=()=>{
      for(const [property,previous] of original) {
        // A consumer can change its inline style during feedback; never restore over that update.
        const current=written.get(property);
        if(!current||!owns(property,current))continue;
        if(previous.value)svg.style.setProperty(property,previous.value,previous.priority);
        else svg.style.removeProperty(property);
      }
      original.clear();written.clear();semantic?.restore();active=false;delete svg.dataset.iconFeedback;
    };
    const lane=createMotionLane(0,progress=>{
      if(!active)return;
      if(semantic){semantic.paint(progress);return}
      // Yield the whole effect before another frame can overwrite a consumer update.
      if([...written].some(([property,value])=>!owns(property,value))){stop();return}
      const pulse=Math.sin(Math.PI*Math.max(0,Math.min(1,progress)));
      const scale=1+pulse*(kind==="press"?-.09:.06);
      const rotate=0;
      // Individual CSS transforms compose with the consumer's transform attribute/style.
      paint("scale",baseScale==="none"?String(scale):baseScale.split(/\s+/).map(value=>`calc(${value} * ${scale})`).join(" "));
      // Preserve a consumer's 3D-axis rotation instead of trying to parse it as a 2D angle.
      if(!baseRotate.includes(" "))paint("rotate",`calc(${baseRotate==="none"?"0deg":baseRotate} + ${rotate}deg)`);
    });
    const stop=()=>{lane.stop();restore()};
    const eligible=()=>{
      const associated=control instanceof HTMLLabelElement?control.control:control;
      return inView&&!document.hidden&&!svg.closest('[inert],[hidden],[data-motion="off"],[data-flow="off"]')&&
        !associated?.matches(':disabled,[disabled],[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])')&&
        !control.matches('[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])')&&
        svg.getClientRects().length>0&&getComputedStyle(svg).visibility!=="hidden";
    };
    const start=(next:typeof kind)=>{
      if(!eligible()){stop();return}
      stop();kind=next;
      const computed=getComputedStyle(svg);baseScale=computed.scale;baseRotate=computed.rotate;
      active=true;svg.dataset.iconFeedback=kind;
      const seconds=duration!==undefined&&Number.isFinite(duration)?Math.max(.08,Math.min(duration,10)):next==="press"?.48:.62;
      const timing=ease==="gentle"?[.22,.72,.22,1] as const:ease==="settle"?[...motionTokens.ease.settle] as const:ease??"linear";
      lane.jump(0);lane.to(1,{duration:seconds,ease:timing},restore);
    };
    const enter=(event:PointerEvent)=>{if(event.pointerType!=="touch")start("hover")};
    const leave=()=>stop();
    const focus=()=>start("focus");
    const blur=(event:FocusEvent)=>{if(!control.contains(event.relatedTarget as Node|null))stop()};
    const key=(event:KeyboardEvent)=>{if(!event.repeat&&(event.key==="Enter"||event.key===" ")){clearTimeout(keyRelease);pendingKeyClick=true;start("press")}};
    const keyup=(event:KeyboardEvent)=>{if(event.key==="Enter"||event.key===" ")keyRelease=setTimeout(()=>{pendingKeyClick=false},0)};
    const click=()=>{if(pendingKeyClick){pendingKeyClick=false;clearTimeout(keyRelease);return}start("press")};
    const visibility=()=>{if(document.hidden)stop()};
    control.addEventListener("pointerenter",enter);control.addEventListener("pointerleave",leave);
    control.addEventListener("focusin",focus);control.addEventListener("focusout",blur);
    control.addEventListener("keydown",key);control.addEventListener("keyup",keyup);control.addEventListener("click",click);
    document.addEventListener("visibilitychange",visibility);
    const intersection=new IntersectionObserver(([entry])=>{inView=entry.isIntersecting;if(!inView)stop()},{threshold:.1});
    intersection.observe(svg);
    const attributes=new MutationObserver(()=>{if(active&&!eligible())stop()});
    for(let ancestor:Element|null=control;ancestor;ancestor=ancestor.parentElement)attributes.observe(ancestor,{attributes:true,attributeFilter:["disabled","aria-disabled","data-disabled","inert","hidden","data-motion","data-flow"]});
    if(control instanceof HTMLLabelElement&&control.control)attributes.observe(control.control,{attributes:true,attributeFilter:["disabled","aria-disabled","data-disabled"]});
    return ()=>{
      control.removeEventListener("pointerenter",enter);control.removeEventListener("pointerleave",leave);
      control.removeEventListener("focusin",focus);control.removeEventListener("focusout",blur);
      control.removeEventListener("keydown",key);control.removeEventListener("keyup",keyup);control.removeEventListener("click",click);clearTimeout(keyRelease);
      document.removeEventListener("visibilitychange",visibility);intersection.disconnect();attributes.disconnect();lane.dispose();restore();
    };
  },[host,enabled,name,duration,ease]);
}

export function iconClassName(size:IconProps["size"]="default",className?:string){return cn("v-icon [width:var(--icon-md)] [height:var(--icon-md)] [stroke:currentColor] [stroke-width:var(--icon-stroke)] [stroke-linecap:round] [stroke-linejoin:round] [fill:none] [flex:none]",size!=="default"&&`-${size}`,className)}
export function Icon({name,size="default",className,draw,feedback=true,treatment="outline",tone="current",feedbackDuration,feedbackEase,style,strokeWidth,ref,...props}:IconProps){
  const {quiet}=useChoreography();
  const host=React.useRef<SVGSVGElement|null>(null);
  const attach=React.useCallback((node:SVGSVGElement|null)=>{host.current=node;const release=assignMotionRef(ref,node);return()=>{host.current=null;release()}},[ref]);
  useIconFeedback(host,feedback&&!quiet&&draw===undefined,name,feedbackDuration,feedbackEase);
  React.useEffect(()=>{
    if(!draw||quiet||!host.current)return;
    const painter=createIconMotionPainter(host.current,name,1,p=>({glyph:{draw:p}}));
    const lane=createMotionLane(0,painter.paint);
    lane.jump(0);lane.to(1,{duration:.5,ease:[.2,.8,.2,1]},painter.restore);
    return()=>{lane.dispose();painter.restore()};
  },[draw,quiet,name]);
  const resolved=canonicalIcon(name);
  const nodes=iconGeometry(name);
  if(!nodes)throw new Error(`Unknown Cojeev icon: ${name}`);
  const accent=tone==="current"?"currentColor":`var(--v-${tone})`;
  return <svg ref={attach} data-slot="icon" data-icon-name={name} data-icon-treatment={treatment} viewBox="0 0 24 24" aria-hidden="true" className={iconClassName(size,className)} style={{...(strokeWidth!==undefined?{strokeWidth}:{}),...style}} {...props}>
    <g data-icon-ink="" style={treatment==="organic"&&tone!=="current"?{color:accent}:undefined}>
      <g data-icon-part="glyph">{(treatment==="organic"?(organicIconGeometry[resolved]??nodes):nodes).map((node,index)=><g key={index} data-icon-part={index}>
        {treatment==="duotone"&&React.createElement(node.tag,{...node.attrs,"data-icon-accent":"",style:{stroke:accent,strokeWidth:4.5,fill:node.tag==="circle"||node.tag==="rect"||node.tag==="ellipse"||node.tag==="polygon"||/[zZ]\s*$/.test(node.attrs.d??"")?accent:"none",opacity:.22}})}
        {renderNode(node,index,treatment==="organic")}
      </g>)}</g>
    </g>
  </svg>
}
/** Semantic action inventory for accessible pickers and documentation. */
export const iconActionDescriptions:Record<string,string>={"check":"The check stroke traces into its complete mark.","check-circle":"The check traces inside a stationary ring.","close":"The cross presses inward and returns.","activity":"The activity line traces across its pulse.","alert":"The warning mark lifts inside its fixed outline.","info":"The information dot lifts inside its fixed circle.","loader":"The loading arc revolves while active.","refresh":"The refresh arrows complete one turn.","search":"The search lens expands inside its handle.","bell":"The bell dome swings against its clapper.","download":"The download arrow moves down into its fixed tray.","upload":"The upload arrow rises out of its fixed tray.","copy":"The front sheet lifts away from the back.","settings":"The gear turns around its fixed hub.","trash":"The lid lifts above its stationary bin.","plus":"The plus makes a quarter turn.","send":"The paper plane travels upward-right.","heart":"The heart gives two restrained beats.","star":"The star expands and returns.","thumbs-up":"The hand lifts above its fixed cuff.",...Object.fromEntries(Object.entries(iconRecipes).map(([name,recipe])=>[name,recipe.description]))};
export const iconActionNames=Object.keys(iconActionDescriptions);
export const iconNames=Array.from(new Set([...Object.keys(iconData),...Object.keys(additionalIcons),...Object.keys(iconAliases),...lucideIconNames])).sort();
export function getIconMotionDescription(name:string):string {
  const canonical=canonicalIcon(name);
  return iconActionDescriptions[name]??iconActionDescriptions[canonical]??familyRecipe(canonical).description;
}
const DiskVariants=cva("v-disk [display:inline-grid] [place-items:center] [width:var(--disk-md)] [height:var(--disk-md)] [border-radius:50%] [background:var(--disk-bg,var(--v-beige))] [color:var(--v-text)] [flex:none]",{variants:{variant:{"default":"","pink":"-pink [--disk-bg:var(--v-pink)] [color:var(--v-on-accent)]","yellow":"-yellow [--disk-bg:var(--v-yellow)] [color:var(--v-on-accent)]","olive":"-olive [--disk-bg:var(--v-olive)] [color:var(--v-on-accent)]","blue":"-blue [--disk-bg:var(--v-blue)] [color:var(--v-on-accent)]","ink":"-ink [--disk-bg:var(--v-ink)] [color:var(--v-on-ink)]","cream":"-cream [--disk-bg:var(--v-canvas)]","beige":"-beige [--disk-bg:var(--v-beige)]"},size:{"default":"","sm":"-sm [width:var(--disk-sm)] [height:var(--disk-sm)]","lg":"-lg [width:var(--disk-lg)] [height:var(--disk-lg)]"}},defaultVariants:{variant:"default",size:"default"}})
export type DiskProps=React.ComponentProps<"span"> & VariantProps<typeof DiskVariants>
export function Disk({className,variant,size,ref,...props}:DiskProps){const morphRef=useMorph<HTMLSpanElement>("icons",ref);return <span ref={morphRef} data-slot="disk"  className={cn(DiskVariants({variant,size}),className)} {...props}/>}
const IconButtonVariants=cva("v-ibtn [display:inline-grid] [place-items:center] [width:var(--ctl-md)] [height:var(--ctl-md)] [border-radius:50%] [color:var(--v-text)] [background:transparent] [box-shadow:inset_0_0_0_1px_var(--v-border)] [transition:background_var(--t-micro),box-shadow_var(--t-micro)]",{variants:{variant:{"default":"","dashed":"-dashed [box-shadow:none] [border:1px_dashed_var(--v-text-2)] [border-color:var(--v-edge)]","ink":"-ink [background:var(--v-ink)] [color:var(--v-on-ink)] [box-shadow:none]","pink":"-pink [background:var(--v-pink)] [box-shadow:none] [color:var(--v-on-accent)]","beige":"-beige [background:var(--v-beige)] [box-shadow:inset_0_0_0_1px_var(--v-edge)] [color:var(--v-on-accent)]","cream":"-cream [background:var(--v-canvas)] [box-shadow:inset_0_0_0_1px_var(--v-edge)]"},size:{"default":"","sm":"-sm [width:var(--ctl-sm)] [height:var(--ctl-sm)]","lg":"-lg [width:var(--ctl-lg)] [height:var(--ctl-lg)]","xl":"-xl [width:var(--dock-action)] [height:var(--dock-action)]"}},defaultVariants:{variant:"default",size:"default"}})
export type IconButtonProps=React.ComponentProps<"button"> & VariantProps<typeof IconButtonVariants>
export function IconButton({className,variant,size,ref,...props}:IconButtonProps){const morphRef=useMorph<HTMLButtonElement>("icons",ref);const ownedRef=useFlowPress(morphRef);return <button ref={ownedRef} data-slot="icon-button" type="button" className={cn(IconButtonVariants({variant,size}),className)} {...props}/>}
