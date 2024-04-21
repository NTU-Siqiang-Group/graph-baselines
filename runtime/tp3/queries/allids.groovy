#META;

p = g.V().id();
idx = 0;

all_id_file_path = System.getenv("ALLIDPATH");

while (p.hasNext()) {
  idx += 1;
  println("vertex id: ${p.next()}");
  if (all_id_file_path.contains("janus")) {
    if (idx >= 1500000) {
      break;
    }
  }
}