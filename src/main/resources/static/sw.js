// Добавляем в начало файла
function parseNotificationData(event) {
    try {
        if (event.data) {
            return event.data.json();
        }
        return {};
    } catch (e) {
        console.error('Error parsing notification:', e);
        return {};
    }
}

self.addEventListener('push', event => {
    console.log('[SW] Push Event Received', event);

    event.waitUntil(
        (async () => {
            try {
                const data = await parseNotificationData(event);
                console.log('[SW] Parsed data:', data);

                await self.registration.showNotification(data.title || 'Новое сообщение', {
                    body: data.body || 'Нажмите, чтобы открыть',
                    data: {
                        bodyToCopy: data.body || ''
                    }
                });
            } catch (error) {
                console.error('[SW] Notification error:', error);
                await self.registration.showNotification('Ошибка', {
                    body: 'Не удалось показать уведомление'
                });
            }
        })()
    );
});

self.addEventListener('notificationclick', event => {
    event.notification.close();
    const text = event.notification.data?.bodyToCopy || '';

    event.waitUntil(
        clients.openWindow(`/copy.html?text=${encodeURIComponent(text)}`)
    );
});