export function round(number, roundLength) {
    const formatter = new Intl.NumberFormat('en-UK', {
        minimumFractionDigits: roundLength,
        maximumFractionDigits: roundLength,
    });

    return formatter.format(number);
}