all_id_file_path = System.getenv("GRAPHMAPPING");
println("get all id from ${all_id_file_path}");

allIds= [];
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}
println("all ids size: ${allIds.size()}");


wcc = [:];
changeMade = true;
for (int i = 0; i < allIds.size(); i++) {
  wcc[allIds[i]] = i;
}
t = System.nanoTime();
while (changeMade) {
  changeMade = false;
  newWcc = [:];
  for (int i = 0; i < allIds.size(); i++) {
    newWcc[allIds[i]] = wcc[allIds[i]];
    bothIds = g.V(allIds[i]).both().id().fold().next();
    minVal = wcc[allIds[i]];
    for (int j = 0; j < bothIds.size(); j++) {
      if (wcc[f.castId(bothIds[j], all_id_file_path)] < minVal) {
        minVal = wcc[f.castId(bothIds[j], all_id_file_path)];
      }
    }
    if (minVal != wcc[allIds[i]]) {
      changeMade = true;
      newWcc[allIds[i]] = minVal;
    }
  }
  wcc = newWcc;
}
exec_time = System.nanoTime() - t;
println("WCC finished in ${exec_time} ns");