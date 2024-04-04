#META;

p = g.V;

depth = 10;

def nextStep(p, i, round) {
  return p.out.gather{ println "v${i} at round ${round}: ${it};"; return it.get(0); }.scatter;
}

for (int i = 0; i < 10000; i++) {
  vid = p.next().id;
  println vid;
  exec = g.v(vid);
  for (int j = 0; j < depth; j++) {
    exec = nextStep(exec, i, j);
  }
  t = System.nanoTime();
  exec.iterate();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + vid + " finished in " + exec_time + " ns");
}
