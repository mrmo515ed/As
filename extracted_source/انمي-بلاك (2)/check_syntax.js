const fs = require('fs');
const html = fs.readFileSync('index.html', 'utf8');
const scriptMatches = html.match(/<script>([\s\S]*?)<\/script>/g);

if (scriptMatches) {
  scriptMatches.forEach((scriptTag, idx) => {
    let scriptContent = scriptTag.replace(/<script>/, '').replace(/<\/script>/, '');
    fs.writeFileSync(`script_${idx}.js`, scriptContent);
    try {
      require('child_process').execSync(`node -c script_${idx}.js`);
      console.log(`Script ${idx} is valid.`);
    } catch (e) {
      console.log(`Syntax error in script ${idx}:`, e.stderr.toString());
    }
  });
}
