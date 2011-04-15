

lookup = {}
lineNumber = 0
for line in open('../res/values/algorithms.xml'):
   lineNumber += 1
   if not line.strip().startswith('<item'):
      continue
   part = line.split(',')
   algorithm = part[5].replace('</item>', '').strip()
   if algorithm in lookup:
      print "[Line %d] Duplicate found on %d for algorithm %s" % (lineNumber,lookup[algorithm],algorithm)
   else:
      lookup.update({algorithm:lineNumber})

