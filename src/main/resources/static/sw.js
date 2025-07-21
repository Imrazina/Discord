// Конфигурация
const DEFAULT_TITLE = 'Новое сообщение';
const DEFAULT_BODY = 'У вас новое сообщение в чате';

// Обработчик push-событий
self.addEventListener('push', event => {
    console.log('[SW] Push Event Received', event);

    // Асинхронная обработка
    event.waitUntil(
        (async () => {
            try {
                // Парсинг данных
                let { title, body } = await parseNotificationData(event);

                // Проверка обязательных полей
                title = title || DEFAULT_TITLE;
                body = body || DEFAULT_BODY;

                console.log(`[SW] Showing notification: ${title} - ${body}`);

                // Показ уведомления с обработкой ошибок
                await self.registration.showNotification(title, {
                    body,
                    vibrate: [200, 100, 200, 100, 200],
                    tag: 'chat-message',
                    data: { url: '/' } // Для открытия при клике
                });

                console.log('[SW] Notification shown successfully');
            } catch (error) {
                console.error('[SW] Notification error:', error);

                // Fallback уведомление
                await self.registration.showNotification(DEFAULT_TITLE, {
                    body: 'Не удалось показать сообщение',
                });
            }
        })()
    );
});

// Парсинг данных уведомления
async function parseNotificationData(event) {
    try {
        if (!event.data) {
            console.warn('[SW] No data in push event');
            return {};
        }

        // Пробуем разные форматы данных
        try {
            return event.data.json();
        } catch (jsonError) {
            console.warn('[SW] Failed to parse as JSON, trying text:', jsonError);
            return {
                body: await event.data.text()
            };
        }
    } catch (error) {
        console.error('[SW] Data parsing failed:', error);
        return {};
    }
}

// Обработчик клика по уведомлению
self.addEventListener('notificationclick', event => {
    event.notification.close();
    event.waitUntil(
        clients.matchAll({ type: 'window' }).then(clientList => {
            for (const client of clientList) {
                if (client.url === '/' && 'focus' in client) {
                    return client.focus();
                }
            }
            if (clients.openWindow) {
                return clients.openWindow('/');
            }
        })
    );
});