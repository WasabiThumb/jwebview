
const bar = document.querySelector("#bar");
const secs = document.querySelector("#secs");
const start = window.performance.now();

function setBarProgress(prog) {
    prog = Math.min(Math.max(prog, 0), 1);
    if (prog === 0) {
        bar.style.opacity = "0";
        return;
    } else if (prog === 1) {
        bar.style.clipPath = "";
        bar.style.opacity = "1";
        return;
    }

    const ang = Math.PI * 2 * prog;
    const cos = Math.cos(ang);
    const sin = Math.sin(ang);

    let f;
    if (ang <= Math.PI / 4) {
        f = 1 / cos;
    } else if (ang <= 3 * Math.PI / 4) {
        f = 1 / sin;
    } else if (ang <= 5 * Math.PI / 4) {
        f = -1 / cos;
    } else if (ang <= 7 * Math.PI / 4) {
        f = -1 / sin;
    } else {
        f = 1 / cos;
    }

    const x = f * sin;
    const y = -f * cos;

    const xp = `${(50 + 50 * x).toFixed(2)}%`;
    const yp = `${(50 + 50 * y).toFixed(2)}%`;
    const p = `${xp} ${yp}`;

    if (ang < Math.PI / 4) {
        bar.style.clipPath = `polygon(50% 0, ${p}, 50% 50%)`;
    } else if (ang < Math.PI * 3 / 4) {
        bar.style.clipPath = `polygon(50% 0, 100% 0, ${p}, 50% 50%)`;
    } else if (ang < Math.PI * 5 / 4) {
        bar.style.clipPath = `polygon(50% 0, 100% 0, 100% 100%, ${p}, 50% 50%)`;
    } else if (ang < Math.PI * 7 / 4) {
        bar.style.clipPath = `polygon(50% 0, 100% 0, 100% 100%, 0 100%, ${p}, 50% 50%)`;
    } else {
        bar.style.clipPath = `polygon(50% 0, 100% 0, 100% 100%, 0 100%, 0 0, ${p}, 50% 50%)`;
    }
    bar.style.opacity = "1";
}

function draw() {
    const elapsed = Math.min(5, (window.performance.now() - start) / 1000);
    secs.innerText = elapsed.toFixed(1);

    const prog = elapsed / 5;
    setBarProgress(prog);

    if (elapsed === 5) {
        complete();
    }
}

function loop() {
    draw();
    window.requestAnimationFrame(loop);
}
window.requestAnimationFrame(loop);
