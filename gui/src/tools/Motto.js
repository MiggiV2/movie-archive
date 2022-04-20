export function getRandomMotto(user) {
    var mottos = getMottos(user.fullName);
    var randomInt = Math.floor(Math.random() * mottos.length);
    return mottos[randomInt];
}

function getMottos(name) {
    return [
        'Schön dich zusehen, ' + name,
        'Willkommen zurück, ' + name,
        'Alles online, ' + name,
        'Freud mich das du da bist, ' + name
    ];
}