#META;

decayFactor = 0.2;

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = f.get_ids_from_files(id_file_path);
// println(allIds);
rand = new Random();

for (int i = 0; i < 100; i++) {
  t = System.nanoTime();
  // vid1 = p.next();
  vid1 = allIds[rand.nextInt() % allIds.size()];
  step = 0;
  // start random walk
  outIds = g.V(vid1).out().id().fold().next();
  startOutIds = outIds;
  while (outIds.size()) {
    def random_out = Math.random();
    if (random_out <= decayFactor) {
      break;
    }
    step += 1;
    rand = new Random();
    idx = rand.nextInt() % outIds.size();
    next_vid = outIds[idx];
    outIds = g.V(next_vid).out().id().fold().next();
    if (outIds.size() == 0) {
      outIds = startOutIds;
    }
  }
  exec_time = System.nanoTime() - t;
  println("Random Walk step: " + step + " finished in " + exec_time + " ns");
}