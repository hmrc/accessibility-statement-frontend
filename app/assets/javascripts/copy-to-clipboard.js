window.GOVUK = window.GOVUK || {}
window.GOVUK.Modules = window.GOVUK.Modules || {};

(function (Modules) {
    function CopyToClipboard ($module) {
        this.$module = $module
        this.$input = this.$module.querySelector('.yaml-to-copy')
        this.$copyButton = this.$module.querySelector('.copy-button')
    }

    CopyToClipboard.prototype.init = function () {
        if (!this.$input || !this.$copyButton) return

        this.$input.addEventListener('click', function () {
            this.$input.select()
        }.bind(this))

        this.$copyButton.addEventListener('click', function (event) {
            event.preventDefault()
            this.$input.select()
            document.execCommand('copy')
        }.bind(this))
    }

    Modules.CopyToClipboard = CopyToClipboard
})(window.GOVUK.Modules)

var element = document.querySelector('[data-module=copy-to-clipboard]')
if(element) { new GOVUK.Modules.CopyToClipboard(element).init() }
