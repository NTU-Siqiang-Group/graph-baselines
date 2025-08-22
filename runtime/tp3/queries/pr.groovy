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

dampingFactor = 0.85;
maxIters = 10;

prs = [:];
initialRank = 1.0 / allIds.size();

for (int i = 0; i < allIds.size(); i++) {
  prs[allIds[i]] = initialRank; // initialize
}

t = System.nanoTime();
for (int i = 0; i < maxIters; i++) {
  newPrs = [:];
  for (int j = 0; j < allIds.size(); j++) {
    inIds = g.V(allIds[j]).in().id().fold().next();
    def double rankSum = 0.0;
    for (int k = 0; k < inIds.size(); k++) {
      rankSum += (double)prs[f.castId(inIds[k], all_id_file_path)];
    }
    newRank = initialRank + dampingFactor * rankSum;
    newPrs[allIds[j]] = newRank;
  }
  prs = newPrs;
  println("Iter ${i} finished");
}
exec_time = System.nanoTime() - t;
println("PR finished in ${exec_time} ns");
