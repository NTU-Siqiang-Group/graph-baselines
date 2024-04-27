#META;

// id_file_path = System.getenv("METAPATH");
// println("get sampled id from ${id_file_path}");

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds= [];
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}
// println(allIds);
rand = new Random();

residuals = [:];
pprs = [:];

max_steps = 10;

alpha = 0.2;
bitmap = [:]

for (int i = 0; i < allIds.size(); i++) {
  residuals[allIds[i]] = 0.0;
  pprs[allIds[i]] = 0.0;
  bitmap[allIds[i]] = 0;
}

empty_bitmap = bitmap;

println("init finished");

// src = sampleIds[rand.nextInt() % sampleIds.size()];
// src = sampleIds[4];
src = allIds[rand.nextInt() % allIds.size()];
residuals[src] = 1.0;
bitmap[src] = 1;
q = [src] as Queue;
// println("${g.V('public.vertex:::369').out().id().fold().next()}")
t = System.nanoTime();
for (int i = 0; i < max_steps; i++) {
  nxtq = [] as Queue;
  println("at step ${i}, queue size: ${q.size()}...")
  while (!q.isEmpty()) {
    j = q.poll();
    // println("    processing ${j}...");
    outIds = g.V(j).out().id().fold().next();
    for (int k = 0; k < outIds.size(); k++) {
      cur_id = outIds[k];
      if (!all_id_file_path.contains('gremlin-pg') && !all_id_file_path.contains('orientdb')) {
        cur_id = cur_id.toInteger();
      } else {
        cur_id = cur_id as String;
      }
      // println(residuals[cur_id]);
      if (residuals[cur_id] == null) {
        residuals[cur_id] = 0;
        bitmap[cur_id] = 0;
        pprs[cur_id] = 0.0;
      }
      residuals[cur_id] += (1 - alpha) * (double)residuals[j] / outIds.size();
      if (residuals[cur_id] != 0 && bitmap[cur_id] == 0) {
        nxtq.offer(cur_id);
        bitmap[cur_id] = 1;
      }
    }
    pprs[j] += alpha * (double)residuals[j];
    residuals[j] = 0;
  }
  bitmap = empty_bitmap;
  q = nxtq;
}
exec_time = System.nanoTime() - t;
println("PPR finished in ${exec_time} ns");